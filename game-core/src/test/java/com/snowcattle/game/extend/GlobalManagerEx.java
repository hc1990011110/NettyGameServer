package com.snowcattle.game.extend;

import com.snowcattle.game.common.enums.BOEnum;
import com.snowcattle.game.common.util.BeanUtil;
import com.snowcattle.game.bootstrap.manager.GlobalManager;
import com.snowcattle.game.service.rpc.client.RpcContextHolder;
import com.snowcattle.game.service.rpc.client.RpcContextHolderObject;

/**
 * Created by jwp on 2017/5/6.
 * 用户拓展
 */
public class GlobalManagerEx extends GlobalManager{

    //拓展使用
    public void initGameManager() throws Exception {
        LocalSpringBeanGameManager localSpringBeanGameManager = (LocalSpringBeanGameManager) BeanUtil.getBean("localSpringBeanGameManager");
        GameManager.getInstance().setLocalSpringBeanGameManager(localSpringBeanGameManager);
        LocalSpringServiceGameManager localSpringServiceGameManager = (LocalSpringServiceGameManager) BeanUtil.getBean("localSpringServiceGameManager");
        GameManager.getInstance().setLocalSpringServiceGameManager(localSpringServiceGameManager);
        RpcContextHolderObject rpcContextHolderObject = new RpcContextHolderObject(BOEnum.WORLD, 8001);
        RpcContextHolder.setContextHolder(rpcContextHolderObject);
    }

    public void startGameManager() throws Exception{
        LocalSpringServiceGameManager localSpringServiceGameManager = GameManager.getInstance().getLocalSpringServiceGameManager();
        localSpringServiceGameManager.start();
    }

    public void stopGameManager() throws Exception{
        LocalSpringServiceGameManager localSpringServiceGameManager = GameManager.getInstance().getLocalSpringServiceGameManager();
        localSpringServiceGameManager.stop();
    }


}
