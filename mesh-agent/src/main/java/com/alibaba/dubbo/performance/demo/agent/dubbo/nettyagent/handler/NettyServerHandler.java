package com.alibaba.dubbo.performance.demo.agent.dubbo.nettyagent.handler;

import com.alibaba.dubbo.performance.demo.agent.util.ChannelContextHolder;
import com.alibaba.dubbo.performance.demo.agent.util.ClientLoopMap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.ReferenceCountUtil;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by maskwang on 18-6-9.
 */
public class NettyServerHandler extends SimpleChannelInboundHandler {

    private static AtomicInteger count = new AtomicInteger(0);
    private static AtomicInteger counter = new AtomicInteger(0);

    private static List<Integer> nums = Arrays.asList(0,1,1,2,2);
    private static ClientLoopMap map = new ClientLoopMap();


    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

        FullHttpRequest httpRequest = (FullHttpRequest) msg;
        try {
            ByteBuf buf = httpRequest.content();    //获取参数
            ByteBuf byteBuf = ctx.channel().alloc().buffer();
            Channel channel = getChannel(ctx.channel().eventLoop());
            int requestId = count.getAndIncrement();
            //保存ctx,方便返回的时候直接取
            ChannelContextHolder.putChannelContext(requestId, ctx);

            byteBuf.writeInt(buf.readableBytes() + 4);
            byteBuf.writeInt(requestId);
            byteBuf.writeBytes(buf);

            ReferenceCountUtil.retain(buf);
            channel.writeAndFlush(byteBuf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public Channel getChannel(EventLoop loop) throws Exception {

        int id = counter.getAndIncrement();
        if (id >= 4) {
            counter.set(0);
            id = 4;
        }
        List<Channel> list = map.get(loop);
        return list.get(nums.get(id));
    }


}
