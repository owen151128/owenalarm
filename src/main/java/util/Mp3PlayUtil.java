package util;

import com.google.common.eventbus.Subscribe;
import event.Mp3Event;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * MP3 File Play Util
 */
public class Mp3PlayUtil {
    private static final Logger logger = Logger.getLogger(Mp3PlayUtil.class.getName());

    private static Mp3PlayUtil instance;

    private static final String ERR_INIT_FAILED = "Mp3 PlayUtil Initialize failed!";
    private static final String ERR_PLAY_FAILED = "Mp3 PlayUtil Play failed!\nmp3 file : ";

    private Player mp3Player = null;
    private String mp3Path = null;

    private boolean isReceiveStopEvent = false;

    private Mp3PlayUtil() {

    }

    /**
     * use Single-tone Pattern
     *
     * @return Mp3PlayUtil Instance
     */
    public static synchronized Mp3PlayUtil getInstance() {
        if (instance == null) {
            instance = new Mp3PlayUtil();
        }

        return instance;
    }

    /**
     * if Player is null, return true.
     * else, return false
     *
     * @return mp3Player is not null
     */
    public boolean isMp3FileSet() {
        return mp3Player != null;
    }

    /**
     * Setting MP3 File
     *
     * @param mp3FilePath MP3 File path (String)
     */
    public void setMp3File(String mp3FilePath) {
        try {
            mp3Path = mp3FilePath;
            mp3Player = new Player(new FileInputStream(mp3FilePath));
        } catch (FileNotFoundException | JavaLayerException e) {
            logger.log(Level.SEVERE, ERR_INIT_FAILED, e);
        }
    }

    /**
     * Play MP3 for once
     * if call This method, mp3File is empty(null)
     *
     * @return if success & play end, return true
     */
    public boolean playMp3() {
        while (!isReceiveStopEvent) {
            try {
                mp3Player = new Player(new FileInputStream(mp3Path));
                mp3Player.play();
            } catch (FileNotFoundException | JavaLayerException e) {
                logger.log(Level.SEVERE, ERR_PLAY_FAILED + mp3Path, e);
                mp3Player.close();
                mp3Player = null;
            }
        }

        return true;
    }

    public void close() {
        mp3Player = null;
        mp3Path = null;
        isReceiveStopEvent = false;
    }

    @Subscribe
    public void receiveEvent(Mp3Event.Event event) {
        switch (event) {
            case stop:
                isReceiveStopEvent = true;
                mp3Player.close();
                mp3Player = null;
        }
    }
}
