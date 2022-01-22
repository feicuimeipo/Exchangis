package com.webank.wedatasphere.exchangis.job.domain.params;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Collection of JobParam<Type>
 */
public class JobParamSet {

    /**
     * Param set
     */
    private Map<String, JobParam<?>> jobParamStore = new ConcurrentHashMap<>();


    public JobParamSet add(JobParam<?> jobParam){
        jobParamStore.put(jobParam.getStrKey(),  jobParam);
        return this;
    }


    public JobParamSet add(JobParamDefine<?> jobParamDefine){
        return add(prepare(jobParamDefine, this));
    }

    public JobParamSet addNonNull(JobParamDefine<?> jobParamDefine){
        JobParam<?> prepared = prepare(jobParamDefine, this);
        if(Objects.nonNull(prepared.getValue())){
            return add(prepared);
        }
        return null;
    }
    /**
     * Append
     * @param key custom key
     * @param jobParam job parameter
     */
    public JobParamSet add(String key, JobParam<?> jobParam){
        jobParamStore.put(key, jobParam);
        return this;
    }

    public JobParamSet add(String key, JobParamDefine<?> jobParamDefine){
        return add(key, prepare(jobParamDefine, this));
    }

    public <T>JobParam<T> load(JobParamDefine<T> jobParamDefine){
        return load(jobParamDefine, this);
    }

    @SuppressWarnings("unchecked")
    public <T>JobParam<T> load(JobParamDefine<T> jobParamDefine, Object source){
        return (JobParam<T>) jobParamStore.compute(jobParamDefine.getKey(), (key, value) -> {
            if (Objects.isNull(value)) {
                value = prepare(jobParamDefine, source);
            }
            return value;
        });
    }

    public JobParamSet combine(JobParamSet paramSet){
        Map<String, JobParam<?>> other = paramSet.jobParamStore;
        this.jobParamStore.putAll(other);
        return this;
    }
    /**
     * Get
     * @param key param key
     * @return param entity
     */
    @SuppressWarnings("unchecked")
    public <T>JobParam<T> get(String key){
        return (JobParam<T>)jobParamStore.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T>JobParam<T> get(String key, Class<T> type){
        return (JobParam<T>)jobParamStore.get(key);
    }
    /**
     * Remove
     * @param key param key
     * @return removed param entity
     */
    @SuppressWarnings("unchecked")
    public <T>JobParam<T> remove(String key){
        return (JobParam<T>) jobParamStore.remove(key);
    }

    /**
     * To List
     * @return param list
     */
    public List<JobParam<?>> toList(){
        return new ArrayList<>(jobParamStore.values());
    }

    public List<JobParam<?>> toList(boolean isTemp){
        return jobParamStore.values().stream().filter(param -> isTemp == param.isTemp()).collect(Collectors.toList());
    }

    /**
     * New param from definition
     * @param jobParam
     */
    private <T>JobParam<T> prepare(JobParamDefine<T> jobParam, Object source){
         return jobParam.newParam(source);
    }

    public static void main(String[] args){
        JobParam<String> ok = new JobParamSet().get("ok");
    }
}
