package com.wrkspot.emp.fence.model.iot;

import com.wrkspot.emp.fence.model.dto.BeaconMetaData;
import org.mongodb.morphia.annotations.Entity;

import java.util.List;
import java.util.Map;

/**
 * Created by murali on 8/21/17.
 * The Beacon Metadata will be written to this class.
 */
@Entity
public class IOTGrid {

    private String identifier;
    private Map<String, List<BeaconMetaData>> axesInfo;


}
