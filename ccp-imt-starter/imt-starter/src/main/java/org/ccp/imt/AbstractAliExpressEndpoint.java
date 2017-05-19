package org.ccp.imt;


import org.springframework.boot.actuate.endpoint.AbstractEndpoint;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * All AliExpress Endpoints should extend this class. It defines the basic structure and the necessary data all AliExpress Endpoints must provide.
 * <br/><br/>
 * After implementing this class, endpoint will produce consistent formatted json data like this:
 * <pre>
 * {
 *     "endpoint": {
 *         "authors": [
 *             "leijuan <jacky.chenlb@alibaba-in.com>"
 *         ],
 *         "docs": "http://gitlab.alibaba-inc.com/spring-boot/spring-boot-starter-tair",
 *         "name": "Tair",
 *         "scm": "git@gitlab.alibaba-inc.com:spring-boot/spring-boot-starter-tair.git",
 *         "version": "1.0.0"
 *     },
 *     "config": {
 *         "uri": "diamond:///mdbcomm-daily?namespace=1"
 *     },
 *     "runtime": {
 *         "caches": [
 *             "user"
 *         ],
 *         "monitor": "https://m.alibaba-inc.com/monitor/view/...",
 *         "namespace": "1"
 *     },
 *     "metrics": {
 *         "tair.user.get.count": 1,
 *         "tair.user.get.fifteenMinuteRate": 0.0010897162674033895,
 *         "tair.user.get.fiveMinuteRate": 0.003144487893819254,
 *         "tair.user.get.meanRate": 0.02074146338690696,
 *         "tair.user.get.oneMinuteRate": 0.012453894499523116
 *     }
 * }
 * </pre>
 *
 * @author juven.xuxb, 12/9/15.
 */
public abstract class AbstractAliExpressEndpoint extends AbstractEndpoint<Map<String, Object>> {

    public AbstractAliExpressEndpoint(String id) {
        super(id, false, true);
    }

    /**
     * Get the name of the endpoint
     *
     * @return endpoint name
     */
    @NotNull
    public abstract String getName();

    /**
     * Get the version of the Endpoint
     *
     * @return current version
     */
    @NotNull
    public abstract String getVersion();

    /**
     * Get the authors of the Endpoint
     *
     * @return author list
     */
    @NotNull
    public abstract List<String> getAuthors();

    /**
     * Get the document url of the Endpoint
     *
     * @return docs
     */
    @NotNull
    public abstract String getDocs();

    /**
     * Get the scm url of the Endpoint
     *
     * @return scm
     */
    @NotNull
    public abstract String getScm();

    /**
     * Get the config of the Endpoint, config usually come from application.properties and only changes after restart
     *
     * @return config object
     */
    public Optional<Object> getConfig() {
        return Optional.empty();
    }

    /**
     * Get the runtime status of the Endpoint, runtime status might change on any visit.
     *
     * @return status
     */
    public Optional<Map<String, Object>> getRuntime() {
        return Optional.empty();
    }

    /**
     * Get the metrics of the Endpoint, metrics might change on any visit.
     *
     * @return metrics
     */
    public Optional<Map<String, Object>> getMetrics() {
        return Optional.empty();
    }


    @Override
    public Map<String, Object> invoke() {
        Map<String, Object> result = new HashMap<>();
        result.put("endpoint", buildEndpoint());
        getMetrics().ifPresent(metrics -> result.put("metrics", metrics));
        getConfig().ifPresent(config -> result.put("config", config));
        getRuntime().ifPresent(status -> result.put("runtime", status));
        return result;
    }

    private Object buildEndpoint() {
        Map<String, Object> result = new HashMap<>();
        result.put("name", getName());
        result.put("version", getVersion());
        result.put("authors", getAuthors());
        result.put("docs", getDocs());
        result.put("scm", getScm());
        return result;
    }
}
