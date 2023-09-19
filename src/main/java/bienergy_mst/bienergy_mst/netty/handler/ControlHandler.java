package bienergy_mst.bienergy_mst.netty.handler;

import java.sql.Connection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Component;

import bienergy_mst.bienergy_mst.model.ChargerDataModel;
import bienergy_mst.bienergy_mst.mysql.QueryExcuteClass;
import bienergy_mst.bienergy_mst.parameter.BackStageStart;
import bienergy_mst.bienergy_mst.parameter.BackStageStop;
import bienergy_mst.bienergy_mst.utils.constant.ChargerEVCConstant;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import lombok.RequiredArgsConstructor;

@Component
@ChannelHandler.Sharable
@RequiredArgsConstructor
public class ControlHandler extends ChannelInboundHandlerAdapter {
	private ChargerDataModel model;
	private QueryExcuteClass execute;
	private String code;
	private ChannelPipeline pipline;
	private String values;
	public Boolean complete = false;
	private String serialCode01;
	private String serialCode02;
	private Boolean play = true;
	private int check = 0;
	public ControlHandler(Connection conn) {
		this.execute = new QueryExcuteClass(conn);
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		this.serialCode01 = "";
		this.serialCode02 = "";
		Executors.newFixedThreadPool(1).execute(() -> {
			while(true) {
				try {Thread.sleep(1000);}catch(Exception e) { e.printStackTrace();}
				if(this.code != null) {
					if(code.equals("0AH02")) {
						if(this.values != null && model != null) {
							byte [] startHexArray = new BackStageStart(values, model.getPlugNo()).start();
							if(execute.callPlugNumber(values.substring(0, 12)).equals("01")) {
								for(int i = 34; i < 49; i++) {
									this.serialCode01 += String.format(ChargerEVCConstant.FORMAT, startHexArray[i]);
								}
							}else {
								for(int i = 34; i < 49; i++) {
									this.serialCode02 += String.format(ChargerEVCConstant.FORMAT, startHexArray[i]);
								}
							}
							ByteBuf writeBuf = Unpooled.directBuffer();
							writeBuf.writeBytes(new BackStageStart(values, model.getPlugNo()).start());
							ChannelFuture cf = ctx.writeAndFlush(writeBuf);
							cf.addListener(new ChannelFutureListener() {
								@Override
								public void operationComplete(ChannelFuture future) throws Exception {
									if(future.isSuccess()) {
										code = null;
										values = null;
										model = null;
									}
								}
							});
						}
					}else if(this.code.equals("0AH03")) {
						if(this.values != null && model != null) {
							ByteBuf writeBuf = Unpooled.directBuffer();
							if(execute.callPlugNumber(values.substring(0, 12)).equals("01")) {;
								writeBuf.writeBytes(new BackStageStop(values, model.getPlugNo(), this.serialCode01).stop());
								this.serialCode01 = "";
							}else {
								writeBuf.writeBytes(new BackStageStop(values, model.getPlugNo(), this.serialCode02).stop());
								this.serialCode02 = "";
							}
							ChannelFuture cf = ctx.writeAndFlush(writeBuf);
							cf.addListener(new ChannelFutureListener() {
								@Override
								public void operationComplete(ChannelFuture future) throws Exception {
									if(future.isSuccess()) {
										code = null;
										values = null;
										model = null;
									}
								}
							});								
						}
					}
				}
			}
		});
	}
	
	public String getSerialCode01() {
		return serialCode01;
	}
	
	public String getSerialCode02() {
		return serialCode02;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public void setChargerDataModel(ChargerDataModel model) {
		this.model = model;
	}
	
	public void setPipline(ChannelPipeline pipline) {
		this.pipline = pipline;
	}
	
	public void setValues(String values) {
		this.values = values;
	}
}	
