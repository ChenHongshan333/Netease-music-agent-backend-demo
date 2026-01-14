package com.example.cs_agent_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "agent.cache")
public class CacheProperties {
    private long ttlSeconds = 600;
    private long refusalTtlSeconds = 30;
    private boolean enabled = false;

    public long getTtlSeconds() { return ttlSeconds; }
    public void setTtlSeconds(long ttlSeconds) { this.ttlSeconds = ttlSeconds; }

    public long getRefusalTtlSeconds() { return refusalTtlSeconds; }
    public void setRefusalTtlSeconds(long refusalTtlSeconds) { this.refusalTtlSeconds = refusalTtlSeconds; }

    public boolean isEnabled() { return enabled; }
public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
