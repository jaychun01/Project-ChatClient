package view;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;

import java.net.Socket;

import application.Main;
public class ClientController {
	
	@FXML
	private TextArea taChat;
	@FXML
	private TextField userName, IPText, portText, tfInput;
	@FXML
	private Button btnConnect, btnSend;

	private Main main;
	
	@FXML
	private void inputAction() {
		tfInput.setOnAction(event->{
			main.send(userName.getText()+": "+tfInput.getText()+"\n");
			tfInput.setText("");
			tfInput.requestFocus();
		});
	}
	@FXML
	private void sendButton() {
		main.send(userName.getText()+": "+tfInput.getText()+"\n");
		tfInput.setText("");
		tfInput.requestFocus();
	}
	@FXML
	private void connectButton() {
		if(userName.getText().trim().isEmpty()) {
			Alert dg = new Alert(Alert.AlertType.CONFIRMATION);
			dg.setTitle("");
			dg.setContentText("Enter your nickname");
			dg.setHeaderText("Nickname");
			dg.show();
			userName.requestFocus();
			return;
		}
		btnConnect.setOnAction(event->{
			if(btnConnect.getText().equals("Connect")) {
				int port=9876;
				boolean ok=false;
				port=Integer.parseInt(portText.getText());
				
				main.startClient(IPText.getText(), port);
				//연결확인을 위한 딜레이를 삽입
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//연결확인
				ok=main.getStatus();
				
				if(ok) {
					btnConnect.setText("Disconnect");
					userName.setEditable(false);
					tfInput.setDisable(false);
					btnSend.setDisable(false);
					tfInput.requestFocus();
				}else {
					Alert dg = new Alert(Alert.AlertType.ERROR);
					dg.setTitle("Connection");
					dg.setContentText("You need to change IP address or Port number. Inquire to server manager.");
					dg.setHeaderText("Check the connection");
					dg.show();
					IPText.requestFocus();
					return;
				}
			}else {
				main.stopClient();
				Platform.runLater(()->{
					taChat.appendText("[ 채팅방 퇴장 ]\n");
				});
				btnConnect.setText("Connect");
				tfInput.setDisable(true);
				btnSend.setDisable(true);
				userName.setEditable(true);
				userName.setText("");
			}
		});
	}
	//textArea 게터
	public TextArea gettaText() {
		return taChat;
	};
	
	public void setMainApp(Main main) {
        this.main = main;
    }
	
}
