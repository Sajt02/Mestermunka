package com.mycompany.myrecipmanagerapplication.service;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ClockService {

    private final Label timeLabel;
    private Timeline timeline;

    public ClockService(Label timeLabel) {
        this.timeLabel = timeLabel;
    }

    public void startClock() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            LocalDateTime now = LocalDateTime.now();
            timeLabel.setText(now.format(formatter));
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void stopClock() {
        if (timeline != null) {
            timeline.stop();
        }
    }
}