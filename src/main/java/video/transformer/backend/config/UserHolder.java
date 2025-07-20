package video.transformer.backend.config;

import java.util.concurrent.ConcurrentHashMap;

public class UserHolder {

    private static ThreadLocal<Integer> holder = new ThreadLocal<>();
    
    // 使用ConcurrentHashMap存储所有用户的位置信息
    private static final ConcurrentHashMap<Integer, UserLocation> userLocations = new ConcurrentHashMap<>();

    public static final  ConcurrentHashMap<Integer, Long> userInRoomMap = new ConcurrentHashMap<>();
    
    // 内部类，用于存储用户位置信息
    public static class UserLocation {
        private Float userLng;
        private Float userLat;
        private Float roomLng;
        private Float roomLat;
        
        public UserLocation(Float userLng, Float userLat, Float roomLng, Float roomLat) {
            this.userLng = userLng;
            this.userLat = userLat;
            this.roomLng = roomLng;
            this.roomLat = roomLat;
        }
        
        public Float getUserLng() { return userLng; }
        public Float getUserLat() { return userLat; }
        public Float getRoomLng() { return roomLng; }
        public Float getRoomLat() { return roomLat; }
        
        public void setUserCoordinates(Float lng, Float lat) {
            this.userLng = lng;
            this.userLat = lat;
        }
        
        public void setRoomCoordinates(Float lng, Float lat) {
            this.roomLng = lng;
            this.roomLat = lat;
        }
    }

    public static void setUserId(Integer id) {
        holder.set(id);
    }

    public static Integer getUserId() {
        return holder.get();
    }

    public static void setUserCoordinates(Integer userId, Float lng, Float lat) {
        userLocations.compute(userId, (key, oldLocation) -> {
            if (oldLocation == null) {
                return new UserLocation(lng, lat, null, null);
            } else {
                oldLocation.setUserCoordinates(lng, lat);
                return oldLocation;
            }
        });
    }

    public static void setRoomCoordinates(Integer userId, Float lng, Float lat) {
        userLocations.compute(userId, (key, oldLocation) -> {
            if (oldLocation == null) {
                return new UserLocation(null, null, lng, lat);
            } else {
                oldLocation.setRoomCoordinates(lng, lat);
                return oldLocation;
            }
        });
    }

    public static UserLocation getUserLocation(Integer userId) {
        return userLocations.get(userId);
    }

    public static void removeUserLocation(Integer userId) {
        userLocations.remove(userId);
    }

    public static void remove() {
        holder.remove();
    }

    public static void clearAllLocations() {
        userLocations.clear();
    }
}
