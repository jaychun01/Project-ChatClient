package application;
	
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import view.ClientController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	private Socket socket;
	private TextArea textArea;
	private boolean ok=false;
	
	//Ŭ���̾�Ʈ ���α׷� �۵�
	public void startClient(String IP, int port) {
		Thread thread = new Thread() {
			public void run() {
				try {
					socket = new Socket(IP, port);
					Platform.runLater(()->{
						textArea.appendText("[ ä�ù� ���� ]\n");
					});
					recieve();
				}catch(Exception e) {
					stopClient();
					Platform.runLater(() -> {
						textArea.appendText("[�������ӽ���]\n");
					});	
					
				}
			}
		};
		thread.start();
	}
	public boolean getStatus() {
		return ok;
	}
	//Ŭ���̾�Ʈ ���α׷� ����
	public void stopClient() {
		ok=false;
		try {
			if(socket !=null&&!socket.isClosed()) {
				socket.close();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	//�����κ��� �޼��� ���޹ޱ�
	public void recieve() {
		ok=true;
		while(true) {
			try {
				InputStream in = socket.getInputStream();
				byte[] buffer = new byte[512];
				int length = in.read(buffer);
				if(length==-1) throw new IOException();
				String message = new String(buffer, 0, length, "UTF-8");
				
				Platform.runLater(()->{
					textArea.appendText(message);
				});
				
			}catch(Exception e) {
				stopClient();
				break;
			}
		}
			
	}
	//������ �޼��� ����
	public void send(String message) {
		Thread thread = new Thread() {
			public void run() {
				try {
					OutputStream out = socket.getOutputStream();
					byte[] buffer = message.getBytes("UTF-8");
					out.write(buffer);
					out.flush();
				}catch(Exception e) {
					stopClient();
				}
			}
		};
		thread.start();
	}
	private Stage primaryStage;
	private BorderPane rootLayout;
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage=primaryStage;
		initChat();
	}
	public void initChat() {
        try {
            // fxml ���Ͽ��� ���� ���̾ƿ��� �����´�.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("/view/ChattingPanel.fxml"));
            rootLayout = (BorderPane) loader.load();
            
            Scene scene = new Scene(rootLayout, 400,600);
            primaryStage.setTitle("[ä�� Ŭ���̾�Ʈ]");
            primaryStage.setOnCloseRequest(event->stopClient());
            primaryStage.setScene(scene);
            
            //ClientController�� ����
            ClientController controller = loader.getController();
            controller.setMainApp(this);
            primaryStage.show();
            textArea=controller.gettaText();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	public static void main(String[] args) {
		launch(args);
	}
}
