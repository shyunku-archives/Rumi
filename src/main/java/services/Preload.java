package services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.RumiBot;
import services.rumi.Rumi;
import util.GPT;
import util.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class Preload {
    public static void initialize() {
        try {
            // get system prompt
            FileInputStream envFileStream = new FileInputStream("src/main/resources/gpt/system-1.txt");
            String systemPrompt = new String(envFileStream.readAllBytes());
            Rumi.instruction = new GPT.Request.CompletionMessage(GPT.Role.SYSTEM, systemPrompt, "Developer");
        }catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
