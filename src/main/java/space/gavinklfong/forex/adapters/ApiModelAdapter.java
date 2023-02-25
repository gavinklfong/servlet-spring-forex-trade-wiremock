package space.gavinklfong.forex.adapters;

import org.mapstruct.Mapper;
import space.gavinklfong.forex.api.dto.ForexRate;
import space.gavinklfong.forex.api.dto.ForexRateBooking;
import space.gavinklfong.forex.api.dto.ForexTradeDeal;
import space.gavinklfong.forex.dto.ForexRateBookingReq;
import space.gavinklfong.forex.dto.ForexTradeDealReq;
import space.gavinklfong.forex.models.TradeAction;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

@Mapper
public interface ApiModelAdapter {
    ForexRateBooking mapModelToDto(space.gavinklfong.forex.models.ForexRateBooking forexRateBooking);
    ForexRate mapModelToDto(space.gavinklfong.forex.models.ForexRate forexRate);
    List<ForexRate> mapModelToForexRateDtoList(List<space.gavinklfong.forex.models.ForexRate> forexRates);
    ForexTradeDeal mapModelToDto(space.gavinklfong.forex.models.ForexTradeDeal forexTradeDeal);
    List<ForexTradeDeal> mapModelToForexTradeDealDtoList(List<space.gavinklfong.forex.models.ForexTradeDeal> forexTradeDeals);
    TradeAction mapDtoToModel(space.gavinklfong.forex.api.dto.TradeAction tradeAction);
    ForexRateBookingReq mapApiDtoToDto(space.gavinklfong.forex.api.dto.ForexRateBookingReq forexRateBookingReq);
    ForexTradeDealReq mapApiDtoToDto(space.gavinklfong.forex.api.dto.ForexTradeDealReq forexTradeDealReq);

    default Instant map(OffsetDateTime offsetDateTime) {
        return offsetDateTime.toInstant();
    }

    default OffsetDateTime map(Instant instant) {
        return OffsetDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
}
