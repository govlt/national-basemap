package lt.lrv.basemap.openmaptiles;

import com.onthegomap.planetiler.ForwardingProfile;

public interface Layer extends
        ForwardingProfile.Handler,
        ForwardingProfile.HandlerForLayer,
        ForwardingProfile.FeatureProcessor {

}