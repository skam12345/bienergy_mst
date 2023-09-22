package bienergy_mst.bienergy_mst.netty.handler;

import java.sql.Connection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import bienergy_mst.bienergy_mst.model.ChargerDataModel;
import bienergy_mst.bienergy_mst.mysql.QueryExcuteClass;
import io.netty.channel.ChannelPipeline;

public class ControlPop {
	private int excel = 0;
	public ControlPop() {
		
	}
	public void run(Connection conn, QueryExcuteClass execute, ChannelPipeline pipline, LoginHandler loginHandler, ControlHandler control, int count) throws Exception {
		if(loginHandler.getCopy() != null) {
			ExecutorService service = Executors.newFixedThreadPool(1);
			service.execute(() -> {
				while(true) {
					try {
						Thread.sleep(4000);
					}catch(Exception e) {
						e.printStackTrace();
					}

					int sendNo = execute.callCommand(loginHandler.getCopy().substring(0, 12));
					ChargerDataModel model = execute.callChargerData(sendNo);
					String code = execute.checkCommand(loginHandler.getCopy().substring(0, 12), model.getPlugNo());
					if(code != null) {
						if(code.equals("0AH02")) {
							List<Integer> sendNoList = execute.callStartCommand(loginHandler.getCopy().substring(0, 12));
							System.out.println("충전 시작합니다.");	
							control.setCode(code);
							loginHandler.setModel(model);
							control.setChargerDataModel(model);
							control.setValues(loginHandler.getCopy());
							System.out.println("명령을 대기합니다.");
						}
						if(code.equals("0AH03")) {
							List<Integer> sendNoList = execute.callStopCommand(loginHandler.getCopy().substring(0, 12));
							System.out.println("충전을 중지합니다.");
							control.setCode(code);
							loginHandler.setModel(model);
							control.setChargerDataModel(model);
							control.setValues(loginHandler.getCopy());
							control.setPipline(pipline);
						}    						
					}else {
						continue;
					}
				}
			});
		}else {
			System.out.printf("잠시만 기다려주세요.\n충전기로부터 서버와의 로그인 대기중 입니다.");
			ExecutorService loginCheckThread = Executors.newFixedThreadPool(1);
			loginCheckThread.execute(() -> {
				try {
					while(loginHandler.getCopy() == null) {
						Thread.sleep(4000);
					}
					System.out.println("서버와 충전기가 로그인 되었습니다.");
					System.out.println("명령 대기중...");
					run(conn, execute, pipline, loginHandler, control, count);
					loginCheckThread.shutdown();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}); 
			Thread.sleep(1000);
		}
    }
}
