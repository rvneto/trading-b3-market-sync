package com.rvneto.b3.market.sync.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class EpochToLocalDateTimeDeserializer extends StdDeserializer<LocalDateTime> {

    private static final ZoneId SAO_PAULO = ZoneId.of("America/Sao_Paulo");
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public EpochToLocalDateTimeDeserializer() {
        super(LocalDateTime.class);
    }

    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (parser.currentToken() == JsonToken.VALUE_NUMBER_INT) {
            // Epoch Unix seconds (original Brapi format)
            long epoch = parser.getLongValue();
            return LocalDateTime.ofInstant(Instant.ofEpochSecond(epoch), SAO_PAULO);
        } else {
            // ISO string format e.g. "2026-05-09T04:18:16.000Z"
            String raw = parser.getText();
            return LocalDateTime.ofInstant(Instant.parse(raw), SAO_PAULO);
        }
    }
}
