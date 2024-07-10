package jawa.sinaukoding.sk.model.request;

public record BuyerCreateBiddingReq(Long auctionId,
                                    Integer bid,
                                    Long bidder) {
}