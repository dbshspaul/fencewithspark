package com.wrkspot.emp.fence.service.impl;

import com.wrkspot.emp.fence.model.UserLocationData;
import com.wrkspot.emp.fence.mongoutil.MongoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * @author prakash
 */
public class HouseKeepingServiceImpl implements Serializable{
    private static final Logger log = LoggerFactory.getLogger(HouseKeepingServiceImpl.class);

    private HouseKeepingServiceImpl() {
    }

    private static class SingletonHelper {
        private static final HouseKeepingServiceImpl INSTANCE = new HouseKeepingServiceImpl();
    }

    public static HouseKeepingServiceImpl getInstance() {
        return SingletonHelper.INSTANCE;
    }

    private MongoUtil mongoUtil = MongoUtil.getInstance();


    public UserLocationData saveOrUpdate(UserLocationData locationInfo) {
        mongoUtil.getMongoDatastore().save(locationInfo);
        return locationInfo;
    }

    public UserLocationData getById(String id) {
        return mongoUtil.getMongoDatastore().find(UserLocationData.class).field("empId").equal(id).get();
    }

}
