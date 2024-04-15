package lt.lrv.basemap.openmaptiles;

import com.onthegomap.planetiler.ForwardingProfile;

interface Layer extends
        ForwardingProfile.Handler,
        ForwardingProfile.HandlerForLayer,
        ForwardingProfile.FeatureProcessor {

}