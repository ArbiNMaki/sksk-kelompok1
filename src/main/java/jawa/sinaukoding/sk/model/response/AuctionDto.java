package jawa.sinaukoding.sk.model.response;

public record AuctionDto(
        Long id,
        String code,
        String name,
        String description,
        Integer minimumPrice,
        String startedAt,
        String endedAt,
        Integer highestBid,
        Long highestBidderId,
        String highestBidderName,
        String status
) {
}
