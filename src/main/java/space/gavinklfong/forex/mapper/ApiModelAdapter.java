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
    ForexRateBookingApiResponse mapModelToDto(ForexRateBooking forexRateBooking);
    ForexRateApiResponse mapModelToDto(ForexRate forexRate);
    List<ForexRateApiResponse> mapModelToForexRateDtoList(List<ForexRate> forexRates);
    ForexTradeDealApiResponse mapModelToDto(ForexTradeDeal forexTradeDeal);
    List<ForexTradeDealApiResponse> mapModelToForexTradeDealDtoList(List<ForexTradeDeal> forexTradeDeals);
    TradeAction mapDtoToModel(ApiTradeAction tradeAction);
    ForexRateBookingReq mapApiDtoToDto(ForexRateBookingApiRequest forexRateBookingReq);
    ForexTradeDealReq mapApiDtoToDto(ForexTradeDealApiRequest forexTradeDealReq);

    default Instant map(OffsetDateTime offsetDateTime) {
        return offsetDateTime.toInstant();
    }

    default OffsetDateTime map(Instant instant) {
        return OffsetDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
}
