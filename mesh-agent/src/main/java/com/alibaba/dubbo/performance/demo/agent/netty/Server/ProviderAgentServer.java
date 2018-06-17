package com.alibaba.dubbo.performance.demo.agent.netty.Server;

import com.alibaba.dubbo.performance.demo.agent.dubbo.RpcClient;
import com.alibaba.dubbo.performance.demo.agent.netty.model.HttpParser;
import com.alibaba.dubbo.performance.demo.agent.netty.model.RequestWrapper;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

import java.util.Map;
import java.util.function.Consumer;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

@ChannelHandler.Sharable
public class ProviderAgentServer extends SimpleChannelInboundHandler<FullHttpRequest> {
    private RpcClient rpcClient = new RpcClient();
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        Map<String, String> data = HttpParser.parse(msg);
        handle(new RequestWrapper(data.get("interface"),
                data.get("method"),
                data.get("parameterTypesString"),
                data.get("parameter")), (result) -> {
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(result.getBytes()));
            response.headers().set(CONTENT_TYPE, "text/plain");
            response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());
            ctx.write(response);
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER);
        },ctx.channel().eventLoop());
    }

    private void handle(RequestWrapper req, Consumer<String> callback, EventLoop loop) throws Exception {
        rpcClient.invoke(req.interfaceName, req.method, req.parameterTypesString, req.parameter, callback,loop);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER);
        ctx.close();
    }
}