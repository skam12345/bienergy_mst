package bienergy_mst.bienergy_mst.netty.handler;
import java.io.FileWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import org.json.simple.JSONObject;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import bienergy_mst.bienergy_mst.model.ChargerDataModel;
import bienergy_mst.bienergy_mst.mysql.MysqlConnection;
import bienergy_mst.bienergy_mst.mysql.QueryExcuteClass;
import bienergy_mst.bienergy_mst.parameter.*;


@ChannelHandler.Sharable
public class LoginHandler extends ChannelInboundHandlerAdapter {
    private String copy;
    private Connection conn;
    private QueryExcuteClass execute;
    private JSONObject obj;
    private ChargerDataModel model;
    private String serialCode;
    private int remain;
    private int readCount;
    private Boolean stopped = true;
    private Boolean complete = false;
    private int remainPower;
	private Boolean login = false, beat = false, record = false, current = false, error = false;
    public LoginHandler() {
    	conn = new MysqlConnection().OpenConnection();
    	execute = new QueryExcuteClass(conn);
    	readCount = 0;
    }
    
    public String getCopy() {
    	return copy;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
		ByteBuf buff = (ByteBuf) msg;
		String bbu = ByteBufUtil.hexDump(buff).toUpperCase();
		List<String> chargerId = execute.callLoginLsit();
		System.out.println(bbu);
		if(bbu.length() == (32 * 2)) {
			if(!login) {
				login = true;
				List<String> data = new ArrayList<String>();
				String sec = "";
				for(int i = 0; i < bbu.length(); i++) {
					sec += bbu.charAt(i);
					if(i % 2 == 0) {
						if(i == bbu.length() - 1) {
							data.add(sec);
						}else {
							data.add(sec + " ");
						}
						sec = "";
					}
				}
				String total = "";
				for(int i = 0; i < data.size(); i++) {
					total = total + data.get(i).toString();
				}
				System.out.println(total);
			}
			copy = bbu.substring(2, 22);
			System.out.println(copy);
			ByteBuf writeBuf = Unpooled.directBuffer();
			writeBuf.writeBytes(new AnsweredLogin(copy).answredLogin());
			ChannelFuture cf = ctx.writeAndFlush(writeBuf);
			cf.addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if(future.isSuccess()) {
						execute.loginUpdate(copy.substring(0, 12));
					}else {
						System.out.println("전송 실패");
					}
				}
			});
    	}else if(bbu.length() == (21 * 2) && bbu.contains(copy.substring(0, 12))) {
			if(!beat) {
				beat = true;
				List<String> data = new ArrayList<String>();
				String sec = "";
				for(int i = 0; i < bbu.length(); i++) {
					sec += bbu.charAt(i);
					if(i % 2 == 0) {
						if(i == bbu.length() - 1) {
							data.add(sec);
						}else {
							data.add(sec + " ");
						}
						sec = "";
					}
				}
				String total = "";
				for(int i = 0; i < data.size(); i++) {
					total = total + data.get(i).toString();
				}
				System.out.println(total);
			}
			ByteBuf writeBuf = Unpooled.directBuffer();
			writeBuf.writeBytes(new HeartBeat(copy).heartBeatAnswered());
			ChannelFuture cf = ctx.writeAndFlush(writeBuf);
			cf.addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if(future.isSuccess()) {
						
					}else {
						System.out.println("전송실패");
					}
				}
			});
		}else if(bbu.length() == 148 && bbu.contains(copy.substring(0, 12))) {
			if(!error) {
				error = true;
				List<String> data = new ArrayList<String>();
				String sec = "";
				for(int i = 0; i < bbu.length(); i++) {
					sec += bbu.charAt(i);
					if(i % 2 == 0) {
						if(i == bbu.length() - 1) {
							data.add(sec);
						}else {
							data.add(sec + " ");
						}
						sec = "";
					}
				}
				String total = "";
				for(int i = 0; i < data.size(); i++) {
					total = total + data.get(i).toString();
				}
				System.out.println(total);
			}
			String copys = bbu.substring(2, 22);
			String alarm_code = bbu.substring(28, 30);
			ByteBuf writeBuf = Unpooled.directBuffer();
			writeBuf.writeBytes(new ErrorReport(copys).errorReportAnswered(alarm_code));
			ChannelFuture cf = ctx.writeAndFlush(writeBuf);
			cf.addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if(future.isSuccess()) {
						
					}else {
						System.out.println("전송실패");
					}
				}
			});
		}else if(bbu.length() == 54 && bbu.substring(26, 28).equals("02") && bbu.contains(copy.substring(0, 12))) {
			if(!current) {
				current = true;
				List<String> data = new ArrayList<String>();
				String sec = "";
				for(int i = 0; i < bbu.length(); i++) {
					sec += bbu.charAt(i);
					if(i % 2 == 0) {
						if(i == bbu.length() - 1) {
							data.add(sec);
						}else {
							data.add(sec + " ");
						}
						sec = "";
					}
				}
				String total = "";
				for(int i = 0; i < data.size(); i++) {
					total = total + data.get(i).toString();
				}
				System.out.println(total);
			}
			execute.sendStartUpdate(copy.substring(0, 12), model.getPlugNo());
			execute.insertReceivce(model, copy.substring(0, 12));
			remain = model.getCharge();
			ByteBuf writeBuf = Unpooled.directBuffer();
			writeBuf.writeBytes(new ReadCurrentData(copy, model.getPlugNo()).requestReadCurrentData());
			ChannelFuture cf = ctx.writeAndFlush(writeBuf);
			cf.addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if(future.isSuccess()) {
						
					}else {
						System.out.println("전송실패");
					}
				}
			});
		}else if(bbu.length() == 54 && bbu.subSequence(26, 28).equals("03") && bbu.contains(copy.substring(0, 12))) {
			current = false;
			if(!current) {
				current = true;
				List<String> data = new ArrayList<String>();
				String sec = "";
				for(int i = 0; i < bbu.length(); i++) {
					sec += bbu.charAt(i);
					if(i % 2 == 0) {
						if(i == bbu.length() - 1) {
							data.add(sec);
						}else {
							data.add(sec + " ");
						}
						sec = "";
					}
				}
				String total = "";
				for(int i = 0; i < data.size(); i++) {
					total = total + data.get(i).toString();
				}
				System.out.println(total);
			}
			execute.sendStopUpdate(copy.substring(0, 12), model.getPlugNo());
			stopped = false;
			if(!complete) {
				execute.stoppedReceive(remain, copy.substring(0, 12), model);
			}else {
				execute.completeReceive(copy.substring(0, 12), model);
			}
		}else if(bbu.length() == 982 && bbu.contains(copy.substring(0, 12))) {
			if(!record) {
				record = true;
				List<String> data = new ArrayList<String>();
				String sec = "";
				for(int i = 0; i < bbu.length(); i++) {
					sec += bbu.charAt(i);
					if(i % 2 == 0) {
						if(i == bbu.length() - 1) {
							data.add(sec);
						}else {
							data.add(sec + " ");
						}
						sec = "";
					}
				}
				String total = "";
				for(int i = 0; i < data.size(); i++) {
					total = total + data.get(i).toString();
				}
				System.out.println(total);
			}
			System.out.println(bbu);
			String copyed = bbu.substring(2, 22);
			String serialCode = bbu.substring(58, 88);
			ByteBuf writeBuf = Unpooled.directBuffer();
			writeBuf.writeBytes(new DataRecord(copyed).dataRecordAnswered(serialCode));
			ChannelFuture cf = ctx.writeAndFlush(writeBuf);
			cf.addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if(future.isSuccess()) {
						
					}else {
						System.out.println("전송실패");
					}
				}
			});
		}else if(bbu.length() ==  23 && bbu.contains(copy.substring(0, 12))) {
			readCount++;
			StringBuffer sb = new StringBuffer();
			String data = bbu.toString().substring(36, 42);
			String point = bbu.toString().substring(34, 36);
			for(int i = data.length(); i > 0; i-=2) {
				sb.append(data.substring(i - 2, i));
			}
			int hexToPoint = Integer.parseInt(point, 16);
			int hexToDec = Integer.parseInt(sb.toString(), 16);
			String pointData = "";
			if(hexToPoint > 10) {
				pointData = Integer.toString(hexToDec) + ".0" + Integer.toString(hexToPoint);
			}else {
				pointData = Integer.toString(hexToDec) + "." + Integer.toString(hexToPoint);
			}
			
			Double pointPower = Double.parseDouble(pointData);
			int power = (int) (pointPower * 1000);
			remainPower = power;
			if(readCount % 3 == 0) {
				execute.updateCharging(power, model.getCharge());
			}
			
			int differ = ((model.getCharge() * 1000) / 100) * 2;
			if(power > ((model.getCharge() * 1000) - differ)) {
				String plugNo = execute.completePlug(power);
				ByteBuf writeBuf = Unpooled.directBuffer();
				writeBuf.writeBytes(new BackStageStop(copy, plugNo, serialCode).stop());
				ChannelFuture cf = ctx.writeAndFlush(writeBuf);
				cf.addListener(new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture future) throws Exception {
						if(future.isSuccess()) {
							complete = true;
						}else {
							System.out.println("전송실패");
						}
					}
				});
			}
			Executors.newFixedThreadPool(1).execute(() -> {
				while(stopped) {
					ByteBuf writeBuf = Unpooled.directBuffer();
					writeBuf.writeBytes(new ReadCurrentData(copy, model.getPlugNo()).requestReadCurrentData());
					ChannelFuture cf = ctx.writeAndFlush(writeBuf);
					cf.addListener(new ChannelFutureListener() {
						@Override
						public void operationComplete(ChannelFuture future) throws Exception {
							if(future.isSuccess()) {
								
							}else {
								System.out.println("전송실패");
							}
						}
					});
					if(!stopped) {
						if(remainPower > (model.getCharge() * 1000) - (((model.getCharge() * 1000) / 100) * 2)) {
							remain = 0;
						}
					}else {
						remain = remainPower;
					}
					try {Thread.sleep(10000); }catch (Exception e) {e.printStackTrace();}
				}
			});
		}
    }
    public void setSerialCode(String serialCode) {
    	this.serialCode = serialCode;
    }
    
    public void setModel(ChargerDataModel model) {
    	this.model = model;
    }
}
