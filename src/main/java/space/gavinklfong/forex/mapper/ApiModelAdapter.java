package space.gavinklfong.forex.mapper;

import org.mapstruct.Mapper;
import space.gavinklfong.forex.api.dto.*;
import space.gavinklfong.forex.domain.dto.ForexRateBookingReq;
import space.gavinklfong.forex.domain.dto.ForexTradeDealReq;
import space.gavinklfong.forex.domain.model.ForexRate;
import space.gavinklfong.forex.domain.model.ForexRateBooking;
import space.gavinklfong.forex.domain.model.ForexTradeDeal;
import space.gavinklfong.forex.domain.model.TradeAction;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

@Mapper
public interface ApiModelAdapter {
    ForexRateBookingApiResponse mapModelToApiResponse(ForexRateBooking forexRateBooking);
    ForexRateApiResponse mapModelToApiResponse(ForexRate forexRate);
    List<ForexRateApiResponse> mapModelToForexRateApiResponse(List<ForexRate> forexRates);
    ForexTradeDealApiResponse mapModelToApiResponse(ForexTradeDeal forexTradeDeal);
    List<ForexTradeDealApiResponse> mapModelToForexTradeDealApiResponse(List<ForexTradeDeal> forexTradeDeals);
    TradeAction mapApiDtoToModel(ApiTradeAction tradeAction);
    ForexRateBookingReq mapApiRequestToDto(ForexRateBookingApiRequest forexRateBookingReq);
    ForexTradeDealReq mapApiRequestToDto(ForexTradeDealApiRequest forexTradeDealReq);

    default OffsetDateTime map(Instant instant) {
        return OffsetDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
}
