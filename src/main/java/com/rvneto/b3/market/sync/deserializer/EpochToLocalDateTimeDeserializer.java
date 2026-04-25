package com.rvneto.b3.market.sync.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class EpochToLocalDateTimeDeserializer extends StdDeserializer<LocalDateTime> {

    private static final ZoneId SAO_PAULO = ZoneId.of("America/Sao_Paulo");

    public EpochToLocalDateTimeDeserializer() {
        super(LocalDateTime.class);
    }

    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        long epoch = parser.getLongValue();
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(epoch), SAO_PAULO);
    }
}
