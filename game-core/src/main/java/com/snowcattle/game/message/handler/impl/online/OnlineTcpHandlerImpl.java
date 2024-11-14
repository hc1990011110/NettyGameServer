package com.snowcattle.game.message.handler.impl.online;

import com.snowcattle.game.common.annotation.MessageCommandAnnotation;
import com.snowcattle.game.common.constant.Loggers;
import com.snowcattle.game.common.enums.BOEnum;
import com.snowcattle.game.jdbc.entity.Order;
import com.snowcattle.game.jdbc.service.impl.OrderService;
import com.snowcattle.game.logic.player.GamePlayer;
import com.snowcattle.game.bootstrap.manager.LocalMananger;
import com.snowcattle.game.message.handler.AbstractMessageHandler;
import com.snowcattle.game.message.logic.tcp.online.client.OnlineLoginClientTcpMessage;
import com.snowcattle.game.message.logic.tcp.online.server.OnlineLoginServerTcpMessage;
import com.snowcattle.game.service.lookup.GamePlayerLoopUpService;
import com.snowcattle.game.service.net.tcp.MessageAttributeEnum;
import com.snowcattle.game.service.message.AbstractNetMessage;
import com.snowcattle.game.service.message.command.MessageCommandIndex;
import com.snowcattle.game.service.net.tcp.session.NettyTcpSession;
import com.snowcattle.game.service.rpc.client.RpcContextHolder;
import com.snowcattle.game.service.rpc.client.RpcContextHolderObject;
import com.snowcattle.game.service.rpc.client.RpcProxyService;
import com.snowcattle.game.service.rpc.service.client.HelloService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by jiangwenping on 17/2/21.
 */
public class OnlineTcpHandlerImpl extends AbstractMessageHandler {

    private final AtomicLong id = new AtomicLong();
    public static long userId = 99999;
    public static int batchStart = 70000000;

    @MessageCommandAnnotation(command = MessageCommandIndex.ONLINE_LOGIN_TCP_CLIENT_MESSAGE)
    public AbstractNetMessage handleOnlineLoginClientTcpMessage(OnlineLoginClientTcpMessage message) throws Exception {
        OnlineLoginServerTcpMessage onlineLoginServerTcpMessage = new OnlineLoginServerTcpMessage();
        long playerId = 6666 + id.incrementAndGet();
        int tocken = 333;
        onlineLoginServerTcpMessage.setPlayerId(playerId);
        onlineLoginServerTcpMessage.setTocken(tocken);
        if (Loggers.sessionLogger.isDebugEnabled()) {
            Loggers.sessionLogger.debug( "playerId " + playerId + "tocken " + tocken + "login");
        }
        NettyTcpSession clientSesion = (NettyTcpSession) message.getAttribute(MessageAttributeEnum.DISPATCH_SESSION);
        GamePlayer gamePlayer = new GamePlayer(clientSesion.getNettyTcpNetMessageSender(), playerId, tocken);
        GamePlayerLoopUpService gamePlayerLoopUpService = LocalMananger.getInstance().getLocalSpringServiceManager().getGamePlayerLoopUpService();
        gamePlayerLoopUpService.addT(gamePlayer);
        RpcProxyService rpcProxyService = LocalMananger.getInstance().getLocalSpringServiceManager().getRpcProxyService();
        RpcContextHolderObject rpcContextHolderObject = new RpcContextHolderObject(BOEnum.WORLD, 8001);
        RpcContextHolder.setContextHolder(rpcContextHolderObject);
        HelloService helloService = rpcProxyService.createProxy(HelloService.class);
        String result = helloService.hello("World");
        logger.debug( "FUCK: " + result);
        ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext(new String[]{"bean/*.xml"});
        OrderService orderService = getOrderService(classPathXmlApplicationContext);
        insertTest(classPathXmlApplicationContext, orderService);
        insertBatchTest(classPathXmlApplicationContext, orderService);
        List<Order> orderList = getOrderList(classPathXmlApplicationContext, orderService);
        deleteBatchTest(classPathXmlApplicationContext, orderService, orderList);
        return onlineLoginServerTcpMessage;
    }

    public OrderService getOrderService(ClassPathXmlApplicationContext classPathXmlApplicationContext) {
        OrderService orderService = (OrderService) classPathXmlApplicationContext.getBean("orderService");
        return orderService;
    }

    public void insertTest(ClassPathXmlApplicationContext classPathXmlApplicationContext, OrderService orderService) {

        int startSize = batchStart;
        int endSize = batchStart+10;

        for (int i = startSize; i < endSize; i++) {
            Order order = new Order();
            order.setUserId(userId);
            order.setId((long) i);
            order.setStatus("测试插入" + i);
            orderService.insertOrder(order);
        }
    }

    public void insertBatchTest(ClassPathXmlApplicationContext classPathXmlApplicationContext, OrderService orderService) throws Exception {
        int startSize = batchStart+100;
        int endSize = startSize + 10;
        List<Order> list = new ArrayList<>();
        for (int i = startSize; i < endSize; i++) {
            Order order = new Order();
            order.setUserId(userId);
            order.setId((long)i);
            order.setStatus("测试列表插入" + i);
            list.add(order);
        }

        orderService.insertOrderList(list);
    }

    public List<Order> getOrderList(ClassPathXmlApplicationContext classPathXmlApplicationContext, OrderService orderService) throws Exception {
        List<Order> order = orderService.getOrderList(userId);
        System.out.println(order);
        return order;
    }

    public void deleteBatchTest(ClassPathXmlApplicationContext classPathXmlApplicationContext, OrderService orderService, List<Order> orderList) throws Exception {
        //test2
        orderService.deleteEntityBatch(orderList);
    }
}
