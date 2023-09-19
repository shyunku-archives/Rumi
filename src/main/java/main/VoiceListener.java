package main;

import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.UserAudio;
import org.jetbrains.annotations.NotNull;

public class VoiceListener implements AudioReceiveHandler {
    @Override
    public boolean canReceiveUser() {
        return AudioReceiveHandler.super.canReceiveUser();
    }

    @Override
    public void handleUserAudio(@NotNull UserAudio userAudio) {
        AudioReceiveHandler.super.handleUserAudio(userAudio);

        // parse user audio stream as text (STT)
    }
}
