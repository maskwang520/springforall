package com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.handler;

import com.alibaba.dubbo.performance.demo.agent.channel.ConsumerAgentChannel;
import com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.connectionpool.NettyPoolClient;
import com.alibaba.dubbo.performance.demo.agent.util.ChannelContextHolder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by maskwang on 18-6-9.
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServerHandler.class);
    private static AtomicInteger count = new AtomicInteger(0);
    private static AtomicInteger c = new AtomicInteger(0);

    private static NettyPoolClient client = new NettyPoolClient();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
       
        FullHttpRequest httpRequest = (FullHttpRequest) msg;
        try {
            ByteBuf buf = httpRequest.content();    //获取参数
            ByteBuf byteBuf = Unpooled.directBuffer();
            Channel channel = ConsumerAgentChannel.getChannel(client);
            int requestId = count.getAndIncrement();

            //LOGGER.info("THE INPUT ID IS {}", requestId);
            ChannelContextHolder.putChannelContext(requestId, ctx);
            byteBuf.writeInt(buf.readableBytes()+4);
            byteBuf.writeInt(requestId);
            byteBuf.writeBytes(buf);
            ReferenceCountUtil.retain(buf);
            //释放有问题
            channel.writeAndFlush(byteBuf);
//                    .addListener(new GenericFutureListener<Future<? super Void>>() {
//                @Override
//                public void operationComplete(Future<? super Void> future) throws Exception {
//                    ChannelPoolMap.get(channel.remoteAddress().toString()).release(channel);
//                }
//            });
//            String []hosts = channel.remoteAddress().toString().split(":");
//            client.poolMap.get(new InetSocketAddress(hosts[0].substring(1,hosts[0].length()),Integer.valueOf(hosts[1]))).release(channel);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //释放请求
            httpRequest.release();
        }
    }

    /**
     * 获取body参数
     *
     * @param request
     * @return
     */
    private String getBody(FullHttpRequest request) {
        ByteBuf buf = request.content();
        return buf.toString(CharsetUtil.UTF_8);
    }




}
