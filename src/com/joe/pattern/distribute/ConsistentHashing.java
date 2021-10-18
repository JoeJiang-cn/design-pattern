package com.joe.pattern.distribute;

import java.util.*;

/**
 * @author Joe
 * TODO description
 * 2021/9/14 9:11
 */
public class ConsistentHashing {
    // 每个真实节点对应10个虚拟节点
    private static final int VIRTUAL_NODES = 20;

    // 真实节点列表
    private static final List<String> REAL_SERVERS = new ArrayList<>();

    // 初始服务器
    private static final String[] SERVERS = {UUID.randomUUID().toString(), UUID.randomUUID().toString(),
            UUID.randomUUID().toString()};

    // <hash(节点名), 节点名>
    private static final TreeMap<Integer, String> HASH_NODES_MAP = new TreeMap<>();

    private static final Map<String, Map<String, Object>> SERVER_CACHE_MAP = new HashMap<>();

    static {
        for (String server : SERVERS) {
            REAL_SERVERS.add(server);
            for (int i = 0; i < VIRTUAL_NODES; i++) {
                String virtualNodesName = server + "[VN" + i + "]";
                HASH_NODES_MAP.put(hash(virtualNodesName), virtualNodesName);
                System.out.println("虚拟节点" + virtualNodesName + " 被添加");
            }
        }
    }

    /**
     * 增加服务器
     * @param server
     */
    public static void addServer(String server) {
        if (!REAL_SERVERS.contains(server)) {
            REAL_SERVERS.add(server);
            // 添加对应的10个虚拟节点
            for (int i = 0; i < VIRTUAL_NODES; i++) {
                String virtualNodesName = server + "[VN" + i + "]";
                HASH_NODES_MAP.put(hash(virtualNodesName), virtualNodesName);
                System.out.println("虚拟节点" + virtualNodesName + " 被添加");
            }
        }
    }

    /**
     * 删除服务器
     * @param server
     */
    public static void removeServer(String server) {
        if (REAL_SERVERS.contains(server)) {
            REAL_SERVERS.remove(server);
            // 删除对应的10个虚拟节点
            for (int i = 0; i < VIRTUAL_NODES; i++) {
                String virtualNodesName = server + "[VN" + UUID.randomUUID() + "]";
                HASH_NODES_MAP.remove(hash(virtualNodesName));
                System.out.println("虚拟节点" + virtualNodesName + " 被移除");
            }
        }
    }

    /**
     * 找到key应该存放在哪个服务器
     * @param key
     * @return
     */
    private static String getServer(String key) {
        Map.Entry<Integer, String> serverEntry = HASH_NODES_MAP.ceilingEntry(hash(key));
        String serverName;
        if (serverEntry != null) {
            // 找到最接近的大于目标的key
            serverName = serverEntry.getValue();
        } else {
            serverName = HASH_NODES_MAP.firstEntry().getValue();
        }
        return serverName.substring(0, serverName.lastIndexOf("[VN"));
    }

    /**
     * 把数据缓存到对应的服务器上
     * @param key
     * @param value
     */
    public static void addCache(String key, Object value) {
        String server = getServer(key);
        Map<String, Object> cache = SERVER_CACHE_MAP.getOrDefault(server, new HashMap<>());
        cache.put(key, value);
        SERVER_CACHE_MAP.put(server, cache);

        System.out.println("数据[key=" + key + ", value=" + value + "]被添加到服务器" + server);
    }

    /**
     * 从对应服务器上获取缓存数据
     * @param key
     * @return
     */
    public static Object getCache(String key) {
        String server = getServer(key);
        Map<String, Object> cache = SERVER_CACHE_MAP.getOrDefault(server, new HashMap<>());
        Object value = cache.get(key);

        if (value != null) {
            System.out.println("数据[key=" + key + ", value=" + value + "]来源于服务器" + server);
        } else {
            System.out.println("数据[key=" + key + "]不在缓存中，从数据库获取...");
            // 模拟从数据库获取数据并保存在缓存中
            // value = key + "-data";
            // cache.put(key, value);
            // SERVER_CACHE_MAP.put(server, cache);
        }

        return value;
    }

    /**
     * FNV1-HASH 32bit
     * 可以替换为其他的hash函数，比如murmurhash等
     * @param key
     * @return
     */
    private static int hash(final String key) {
        final int p = 16777619;
        int hash = (int) 2166136261L;
        for(int i = 0; i < key.length(); i++)
            hash = (hash ^ key.charAt(i)) * p;
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;

        // 算出来值为负数就取绝对值
        if(hash < 0)
            hash = Math.abs(hash);
        return hash;
    }

    public static void main(String[] args) {
        String[] keyArray = new String[10000];
        // 模拟添加10000个数据
        System.out.println("添加缓存");
        for (int i = 0; i < 10000; i++) {
            keyArray[i] = UUID.randomUUID().toString();
            addCache(keyArray[i], keyArray[i] + "-data");
        }

        // 输出此时数据的分布
        for (Map.Entry<String, Map<String, Object>> entry : SERVER_CACHE_MAP.entrySet()) {
            System.out.println("服务器" + entry.getKey() + "有" + entry.getValue().size() + "个Key");
        }

//        // 获取其中2000个数据
//        for (int i = 4000; i < 6000; i++) {
//            getCache(keyArray[i]);
//        }
//
//        // 加一个服务器
//        addServer("192.168.1.7");
//
//        // 获取其中2000个数据，此时有部分数据会前往新加的服务器查找，这部分数据会找不到
//        int missing = 0;
//        for (int i = 4000; i < 6000; i++) {
//            if (getCache(keyArray[i]) == null) {
//                missing++;
//            }
//        }
//        System.out.println("这2000个缓存中有" + missing + "个失效了");
    }
}
