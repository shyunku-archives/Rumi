package services.rumi;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import util.Ranged;

import java.io.IOException;

public class StdRanged extends Ranged {
    public StdRanged() {
        super(0, -1, 1);
    }

    public static class Serializer extends JsonSerializer<StdRanged> {
        @Override
        public void serialize(StdRanged value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeNumber(value.getCurrent());
        }
    }

    public static class Deserializer extends JsonDeserializer<StdRanged> {
        @Override
        public StdRanged deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, com.fasterxml.jackson.core.JsonProcessingException {
            StdRanged obj = new StdRanged();
            obj.setCurrent(p.getDoubleValue());
            return obj;
        }
    }
}
