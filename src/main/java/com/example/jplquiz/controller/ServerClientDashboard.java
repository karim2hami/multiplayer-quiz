package com.example.jplquiz.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class ServerClientDashboard {

  @FXML
  private Button btn_start;

  @FXML
  private Label lb_playerCounter;

  public void setLb_playerCounter(int number) {
    lb_playerCounter.setText(Integer.toString(number));
  }
}
