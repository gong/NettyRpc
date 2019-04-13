package com.xxx.rpc.common.util;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;

import com.xxx.rpc.common.constant.Type;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;


public class SelectTrans {

    private static Type getOSTypeByOSName(){
        String os = System.getProperty("os.name").toLowerCase();
        if (os.startsWith("windows")){
            return Type.WINDOWS;
        } else if (os.startsWith("linux")) {
            return Type.LINUX;
        } else {
            return Type.UNKNOWN;
        }
    }

    public static Class<? extends SocketChannel> getClientChannelClassByOS(){
        Type type = getOSTypeByOSName();
        switch (type){
            case LINUX:
                return EpollSocketChannel.class;
            case WINDOWS:
                default:
                    return NioSocketChannel.class;
        }
    }

    public static Class<? extends ServerChannel> getServerChannelClassByOS() {
        Type type = getOSTypeByOSName();
        switch (type){
            case LINUX:
                return EpollServerSocketChannel.class;
            case WINDOWS:
            default:
                return NioServerSocketChannel.class;
        }
    }

    public static EventLoopGroup getEventLoopGroupByOSName(){
        Type type = getOSTypeByOSName();
        switch (type){
            case LINUX:
                return new EpollEventLoopGroup();
            case WINDOWS:
            default:
                return new NioEventLoopGroup();
        }
    }

    public static void main(String[] args) {
        System.out.println(getOSTypeByOSName());
    }

}
