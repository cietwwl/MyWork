package iyunu.NewTLOL.base.net.socket;

import iyunu.NewTLOL.manager.ServerManager;

import java.io.UnsupportedEncodingException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.handler.codec.frame.CorruptedFrameException;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liteProto.LlpJava;
import com.liteProto.LlpMessage;

/**
 * 协议格式为: frameLength(short) + msgName(utf) + llpMsg
 * 
 */
public class LlpDecoder extends FrameDecoder {

	private static Logger logger = LoggerFactory.getLogger(LlpDecoder.class);

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {
		// logger.info("new data coming...");
		if (buffer.readableBytes() < 2) {
			return null;
		}
		// 读取frame长度字段
		short frameLength = buffer.getShort(buffer.readerIndex());

		// logger.debug("frameLength: " + frameLength);

		if (frameLength <= 4) {
			buffer.skipBytes(2); // 长度标识小于0 跳过长度
			Channels.fireExceptionCaught(channel, new CorruptedFrameException("negative length field: " + frameLength));
		}

		// 判断length长度的合理性一条协议长度至少为4(frameLength + utfLength)并且小于Short.MAX_VALUE
		if (frameLength > Short.MAX_VALUE) { // 长度大于合理值关闭此连接
			Channels.fireExceptionCaught(channel, new TooLongFrameException("frame length exceeds " + Short.MAX_VALUE));
		}

		if (buffer.readableBytes() < frameLength + 2) { // +2为加上frameLength本身short的长度
			return null;
		}

		// logger.debug("!!new msg received!!");
		LlpMessage msg = getLlpMessage(channel, buffer, buffer.readerIndex(), frameLength);
		buffer.readerIndex(buffer.readerIndex() + frameLength + 2); // 完整读完一条消息length(short)
																	// + data
		return msg;
	}

	/**
	 * 从ChannelBuffer中获取LlpMessage ChannelBuffer中内容格式为 msgName(utf) + llpMsg
	 * 
	 * @param channel
	 *            {@link Channel}
	 * @param buffer
	 *            网络发送过来的LlpMessage数据
	 * @param index
	 *            buffer起始读取位置
	 * @param length
	 *            msgName(utf) + llpMsg总长度
	 * @return LlpMessage
	 */
	private LlpMessage getLlpMessage(Channel channel, ChannelBuffer buffer, int index, int length) {
		// 读取UTF 协议名称
		short strLen = buffer.getShort(index + 2);
		String protocolName = getUTFStr(buffer, index + 4, strLen);
		// logger.debug("protocolName: " + protocolName);
		if (protocolName == null) {
			Channels.fireExceptionCaught(channel, new CorruptedFrameException("uft encoding error!"));
			return null;
		}
		// 生成llpMessage
		int msgLen = length - strLen - 2; // msgLen = length - utfLen
		int msgStartPos = index + strLen + 4; // msgLen(short) + utfLen
		ChannelBuffer frame = buffer.factory().getBuffer(msgLen);
		frame.writeBytes(buffer, msgStartPos, msgLen);
		LlpMessage msg = null;
		try {
			msg = LlpJava.instance().getMessage(protocolName, frame.array());
		} catch (Exception e) {
			Channels.fireExceptionCaught(channel, new CorruptedFrameException("phrase llp message failed! " + protocolName));
		}
		return msg;
	}

	/**
	 * 读取UTF格式string
	 * 
	 * @param buffer
	 *            {@link ChannelBuffer}
	 * @param index
	 *            读取的开始位置
	 * @param strLen
	 *            string长度
	 * @return 读取的字符串
	 */
	private String getUTFStr(ChannelBuffer buffer, int index, int strLen) {
		if (strLen > 0) {
			byte[] data = new byte[strLen];
			buffer.getBytes(index, data, 0, strLen);
			try {
				return new String(data, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				logger.error("uft encoding error!", e);
				return null;
			}
		}
		return null;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		ServerManager.instance().offline(ctx.getChannel(), "异常断开连接");
		logger.error("llp decode error!", e.getCause());
	}
}
