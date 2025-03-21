package jawa.sinaukoding.sk.service;

import jawa.sinaukoding.sk.entity.Auction;
import jawa.sinaukoding.sk.entity.AuctionBid;
import jawa.sinaukoding.sk.entity.User;
import jawa.sinaukoding.sk.model.Authentication;
import jawa.sinaukoding.sk.model.request.BuyerCreateBiddingReq;
import jawa.sinaukoding.sk.model.request.SellerCreateAuctionReq;
import jawa.sinaukoding.sk.model.Response;
import jawa.sinaukoding.sk.model.request.UpdateStatusReq;
import jawa.sinaukoding.sk.model.response.AuctionDto;
import jawa.sinaukoding.sk.model.response.BiddingDto;
import jawa.sinaukoding.sk.repository.AuctionRepository;
import jawa.sinaukoding.sk.repository.UserRepository;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.*;

@Service
public final class AuctionService extends AbstractService {

    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;


    public AuctionService(final Environment env, final AuctionRepository auctionRepository, UserRepository userRepository) {
        this.auctionRepository = auctionRepository;
        this.userRepository = userRepository;
    }

    private static String generateCode() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public Response<Object> createAuction(final Authentication authentication, final SellerCreateAuctionReq req) {
        return precondition(authentication, User.Role.SELLER).orElseGet(() -> {
            if (req == null) {
                return Response.badRequest();
            }

            OffsetDateTime startedAt = OffsetDateTime.parse(req.startedAt());
            OffsetDateTime endedAt = OffsetDateTime.parse(req.endedAt());
            OffsetDateTime now = OffsetDateTime.now();

            long durationInMinutes = Duration.between(startedAt, endedAt).toMinutes();

            if (durationInMinutes > 1440) {
                return Response.create("05", "02", "Tidak boleh melebihi dari 24 jam", null);
            }

            if (startedAt.isBefore(now)) {
                return Response.create("05", "02", "Tidak bisa membuat auction dengan waktu mulai di masa lalu", null);
            }

            Auction newAuction = new Auction(
                    generateCode(),
                    req.name(),
                    req.description(),
                    req.minimumPrice(),
                    startedAt,
                    endedAt,
                    authentication.id()
            );

            final long saved = auctionRepository.saveAuction(newAuction);

            if (saved == 0L) {
                return Response.create("05", "01", "Gagal membuat lelang.", null);
            } else {
                return Response.create("05", "00", "Sukses membuat lelang.", saved);
            }
        });
    }

    public Response<Object> listAuction(final Authentication authentication, final int page, final int size, final String name) {
        return precondition(authentication, User.Role.ADMIN, User.Role.SELLER, User.Role.BUYER).orElseGet(() -> {
            if (page <= 0 || size <= 0) {
                return Response.badRequest();
            }

            final List<Auction> auctions = auctionRepository.listAuction(page, size, name);

            if (auctions.isEmpty()) {
                return Response.create("09", "01", "Auction tidak ada", null);
            }

            final List<AuctionDto> dto = auctions.stream()
                    .map(auction -> new AuctionDto(
                            auction.id(),
                            auction.code(),
                            auction.name(),
                            auction.description(),
                            auction.offer(),
                            auction.startedAt().toString(),
                            auction.endedAt().toString(),
                            auction.highestBid(),
                            auction.highestBidderId(),
                            auction.highestBidderName(),
                            auction.status().toString()
                    )).toList();

            return Response.create("09", "00", "Sukses", dto);
        });
    }

    public Response<Object> getAuctionById(final Authentication authentication, final Long id) {
        return precondition(authentication, User.Role.ADMIN, User.Role.SELLER, User.Role.BUYER).orElseGet(() -> {
            List<Auction> auctions = auctionRepository.findById(id);

            if (auctions.isEmpty()) {
                return Response.create("09", "01", "Auction not found", null);
            }

            Auction auction = auctions.get(0);
            List<AuctionBid> biddings = auctionRepository.findBiddingByAuctionId(auction.id());

            AuctionDto auctionDto = new AuctionDto(
                    auction.id(),
                    auction.code(),
                    auction.name(),
                    auction.description(),
                    auction.offer(),
                    auction.startedAt().toString(),
                    auction.endedAt().toString(),
                    auction.highestBid(),
                    auction.highestBidderId(),
                    auction.highestBidderName(),
                    auction.status().toString()
            );

            List<BiddingDto> biddingDto = biddings.stream()
                    .map(bid -> new BiddingDto(
                            bid.id(),
                            bid.auctionId(),
                            bid.bid(),
                            bid.bidder(),
                            bid.createdAt().toString()
                    )).toList();

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("auction", auctionDto);
            responseData.put("bidding", biddingDto);

            return Response.create("09", "00", "Sukses", responseData);
        });
    }

    public Response<Object> updateAuctionStatus(Authentication authentication, UpdateStatusReq req) {
        return precondition(authentication, User.Role.ADMIN).orElseGet(() -> {
            Auction.Status newStatus = req.status();

            List<Auction> auctions = auctionRepository.findById(req.id());
            if (auctions.isEmpty()) {
                return Response.create("07", "02", "Auction tidak ditemukan", null);
            }

            Auction auction = auctions.get(0);

            if (auction.status() == Auction.Status.APPROVED || auction.status() == Auction.Status.REJECTED) {
                return Response.create("07", "03", "Status auction sudah DIUBAH dan tidak bisa diubah lagi", null);
            }

            Auction updatedAuction = new Auction(
                    auction.id(),
                    auction.code(),
                    auction.name(),
                    auction.description(),
                    auction.offer(),
                    auction.highestBid(),
                    auction.highestBidderId(),
                    auction.highestBidderName(),
                    newStatus,
                    auction.startedAt(),
                    auction.endedAt(),
                    auction.createdBy(),
                    auction.updatedBy(),
                    auction.deletedBy(),
                    auction.createdAt(),
                    OffsetDateTime.now(),
                    auction.deletedAt()
            );

            long updated = auctionRepository.updateAuctionStatus(updatedAuction);

            if (updated == 1L) {
                return Response.create("07", "00", "Sukses", updated);
            } else {
                return Response.create("07", "01", "Gagal update Status", null);
            }
        });
    }

    public Response<Object> closeAuctionStatus(Authentication authentication, UpdateStatusReq req) {
        return precondition(authentication, User.Role.ADMIN, User.Role.SELLER).orElseGet(() -> {
            Auction.Status newStatus = req.status();

            List<Auction> auctions = auctionRepository.findById(req.id());
            if (auctions.isEmpty()) {
                return Response.create("07", "02", "Auction tidak ditemukan", null);
            }

            Auction auction = auctions.get(0);

            if (auction.status() == Auction.Status.CLOSED) {
                return Response.create("07", "03", "Status auction sudah CLOSED dan tidak bisa diubah lagi", null);
            }

            Auction updatedAuction = new Auction(
                    auction.id(),
                    auction.code(),
                    auction.name(),
                    auction.description(),
                    auction.offer(),
                    auction.highestBid(),
                    auction.highestBidderId(),
                    auction.highestBidderName(),
                    newStatus,
                    auction.startedAt(),
                    auction.endedAt(),
                    auction.createdBy(),
                    auction.updatedBy(),
                    auction.deletedBy(),
                    auction.createdAt(),
                    OffsetDateTime.now(),
                    auction.deletedAt()
            );

            long updated = auctionRepository.closeAuctionStatus(updatedAuction);

            if (updated == 1L) {
                return Response.create("07", "00", "Sukses", updated);
            } else {
                return Response.create("07", "01", "Gagal Close Status", null);
            }
        });
    }

    public Response<Object> createBidding(final Authentication authentication, final BuyerCreateBiddingReq req, final Long id) {
        return precondition(authentication, User.Role.BUYER).orElseGet(() -> {
            if (req == null) {
                return Response.badRequest();
            }

            List<Auction> auctions = auctionRepository.findById(id);
            if (auctions.isEmpty()) {
                return Response.create("05", "01", "Auction tidak ditemukan.", null);
            }

            Auction auction = auctions.get(0);

            if (req.bid() <= auction.offer()) {
                return Response.create("07", "04", "Tidak bisa melakukan bidding karena bid lebih kecil daripada offer.", null);
            }

            final Optional<User> userOpt = userRepository.findById(authentication.id());
            final User user = userOpt.get();

            if (auction.status() == Auction.Status.WAITING_FOR_APPROVAL || auction.status() == Auction.Status.CLOSED) {
                String message = "Tidak bisa melakukan bidding karena status auction (" + auction.status() + ")";
                return Response.create("07", "03", message, null);
            }

            final AuctionBid bid = new AuctionBid(
                    null,
                    id,
                    req.bid(),
                    req.bidder(),
                    OffsetDateTime.now()
            );

            final Long saved = auctionRepository.saveBidding(bid);

            if (saved == 0L) {
                return Response.create("05", "01", "Gagal membuat bidding.", null);
            } else {
                if (req.bid() > auction.highestBid()) {
                    auctionRepository.updateHighestBid(id, req.bid(), req.bidder(), user.name());
                }

                List<AuctionBid> biddings = auctionRepository.findBiddingByAuctionId(auction.id());

                List<BiddingDto> biddingDto = biddings.stream()
                        .map(b -> new BiddingDto(
                                b.id(),
                                b.auctionId(),
                                b.bid(),
                                b.bidder(),
                                b.createdAt().toString()
                        )).toList();

                Map<String, Object> responseData = new HashMap<>();
                responseData.put("bidding", biddingDto);

                return Response.create("05", "00", "Sukses membuat bidding.", responseData);
            }
        });
    }
}