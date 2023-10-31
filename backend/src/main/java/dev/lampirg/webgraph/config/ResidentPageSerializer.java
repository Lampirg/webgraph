package dev.lampirg.webgraph.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import dev.lampirg.webgraph.model.Resident;
import dev.lampirg.webgraph.model.ResidentsPageWithPrevNextRels;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.data.domain.Page;

import java.io.IOException;

@JsonComponent
public class ResidentPageSerializer extends StdSerializer<ResidentsPageWithPrevNextRels> {


    public ResidentPageSerializer() {
        super(ResidentsPageWithPrevNextRels.class);
    }

    @Override
    public void serialize(ResidentsPageWithPrevNextRels value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        writeMainObject(value, gen);
        gen.writeEndObject();
    }

    private void writeMainObject(ResidentsPageWithPrevNextRels value, JsonGenerator gen) throws IOException {
        writeData(value, gen);
        writePageInfo(value, gen);
    }

    private void writeData(ResidentsPageWithPrevNextRels value, JsonGenerator gen) throws IOException {
        gen.writeObjectField("data", value.page().get().toArray());
    }

    private void writePageInfo(ResidentsPageWithPrevNextRels value, JsonGenerator gen) throws IOException {
        Page<Resident> page = value.page();
        gen.writeObjectFieldStart("page");
        gen.writeNumberField("current", page.getNumber());
        gen.writeNumberField("total", page.getTotalPages());
        gen.writeBooleanField("hasNext", page.hasNext());
        gen.writeBooleanField("hasPrev", page.hasPrevious());
        gen.writeStringField("next", value.next());
        gen.writeStringField("prev", value.prev());
        gen.writeEndObject();
    }
}
