package bienergy_mst.bienergy_mst.netty.handler;

import java.io.FileReader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Component;

import bienergy_mst.bienergy_mst.model.ChargerDataModel;
import bienergy_mst.bienergy_mst.mysql.MysqlConnection;
import bienergy_mst.bienergy_mst.mysql.QueryExcuteClass;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import lombok.RequiredArgsConstructor;

@Component
public class SimpleChannelInitializer extends ChannelInitializer<SocketChannel>{
	private List<String> isLogin;
	private FileReader reader;
	private JSONParser parser;
	private int count = 0;
	private ArrayList<LoginHandler> loginList = new ArrayList<LoginHandler>();
	private ArrayList<ControlHandler> controlList = new ArrayList<ControlHandler>();
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        try {
        	ChannelPipeline pipline01 = ch.pipeline();
    		System.out.println(ch.remoteAddress());
    		LoginHandler loginHandler = new LoginHandler();
    		loginList.add(loginHandler);
    		pipline01.addFirst(loginHandler);
			Connection conn  = new MysqlConnection().OpenConnection();
			QueryExcuteClass execute = new QueryExcuteClass(conn);
			isLogin =  execute.callIsLogin();
			ControlHandler control = new ControlHandler(conn);
			controlList.add(control);		
			pipline01.addLast(control);
			boolean flag = true;
			for(String login : isLogin) {
				if(login.equals("Y")) {
					isLogin =  execute.callIsLogin();
					flag = false;
				}else {
					flag = true;
				}
			}
			System.out.println("충전기 하나 로그인 되었습니다.");
			System.out.println("명령을 대기합니다.");
			new ControlPop().run(conn, execute, pipline01, loginList.get(count), controlList.get(count), count);
			count++;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}