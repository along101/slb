package com.along101.wormhole.admin.manager.appsite;

import com.along.wormhole.admin.common.constant.OnlineStatusEnum;
import com.along.wormhole.admin.common.utils.ConvertUtils;
import com.along.wormhole.admin.dao.AppSiteRepository;
import com.along.wormhole.admin.dto.PageDto;
import com.along.wormhole.admin.dto.appsite.AppSiteDto;
import com.along.wormhole.admin.dto.appsite.AppSiteQuery;
import com.along.wormhole.admin.entity.AppSiteEntity;
import com.along101.wormhole.admin.common.constant.OnlineStatusEnum;
import com.along101.wormhole.admin.common.utils.ConvertUtils;
import com.along101.wormhole.admin.dao.AppSiteRepository;
import com.along101.wormhole.admin.dto.PageDto;
import com.along101.wormhole.admin.dto.appsite.AppSiteDto;
import com.along101.wormhole.admin.dto.appsite.AppSiteQuery;
import com.along101.wormhole.admin.entity.AppSiteEntity;
import com.along101.wormhole.admin.common.constant.OnlineStatusEnum;
import com.along101.wormhole.admin.common.utils.ConvertUtils;
import com.along101.wormhole.admin.dao.AppSiteRepository;
import com.along101.wormhole.admin.dto.PageDto;
import com.along101.wormhole.admin.dto.appsite.AppSiteDto;
import com.along101.wormhole.admin.dto.appsite.AppSiteQuery;
import com.along101.wormhole.admin.entity.AppSiteEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by yinzuolong on 2017/7/26.
 */
@Component
public class AppSiteManager {

    @Autowired
    private AppSiteRepository appSiteRepository;

    @Transactional
    public Optional<Long> increaseVersion(Long appSiteId) {
        appSiteRepository.increaseVersion(appSiteId);
        AppSiteEntity entity = appSiteRepository.getOne(appSiteId);
        return Optional.ofNullable(entity).map(AppSiteEntity::getOnlineVersion);
    }

    @Transactional
    public AppSiteDto saveAppSite(AppSiteDto appSiteDto) {
        AppSiteEntity appSiteEntity = ConvertUtils.convert(appSiteDto, AppSiteEntity.class);
        appSiteDto.setOfflineVersion(appSiteDto.getOnlineVersion());
        AppSiteEntity newEntity = appSiteRepository.save(appSiteEntity);
        //由于jpa缓存问题，这里要把单独刷新的字段重新刷新下
        appSiteRepository.lock(newEntity.getId());
        newEntity = appSiteRepository.getOne(newEntity.getId());
        return ConvertUtils.convert(newEntity, appSiteDto);
    }

    @Transactional
    public void removeAppSite(Long appSiteId) {
        appSiteRepository.removeAppSite(appSiteId);
        appSiteRepository.flush();
    }

    public Optional<Integer> getStatus(Long appSiteId) {
        AppSiteEntity entity = appSiteRepository.findOne(appSiteId);
        return Optional.ofNullable(entity).map(AppSiteEntity::getStatus);
    }

    public void updateStatus(Long appSiteId, OnlineStatusEnum statusEnum) {
        appSiteRepository.updateStatus(appSiteId, statusEnum.getValue());
    }

    public List<AppSiteDto> getBySlbId(Long slbId) {
        List<AppSiteEntity> entities = appSiteRepository.findBySlbId(slbId);
        return ConvertUtils.convert(entities, AppSiteDto.class);
    }

    public Optional<AppSiteDto> getById(Long appSiteId) {
        AppSiteEntity entity = appSiteRepository.findOne(appSiteId);
        return Optional.ofNullable(entity).map(e -> ConvertUtils.convert(e, AppSiteDto.class));
    }

    public List<AppSiteDto> getByIds(List<Long> appSiteIds) {
        List<AppSiteEntity> entities = appSiteRepository.findByAppIds(appSiteIds);
        return ConvertUtils.convert(entities, AppSiteDto.class);
    }

    public List<AppSiteDto> getAll() {
        List<AppSiteEntity> entities = appSiteRepository.findAll();
        return ConvertUtils.convert(entities, AppSiteDto.class);
    }

    public PageDto<AppSiteDto> getByPage(int page, int size) {
        PageRequest pageRequest = new PageRequest(page, size);
        Page<AppSiteEntity> entities = appSiteRepository.findAll(pageRequest);
        return ConvertUtils.convertPage(entities, AppSiteDto.class);
    }

    public Optional<AppSiteDto> getByAppId(Long slbId, String appId) {
        AppSiteEntity entity = appSiteRepository.findByAppId(slbId, appId);
        return Optional.ofNullable(entity).map(e -> ConvertUtils.convert(e, AppSiteDto.class));
    }

    //TODO 根据条件查询站点
    public PageDto<AppSiteDto> getByConditions(AppSiteQuery appSiteQueryConditionDto, PageRequest page) {
        AppSiteQuery condition = appSiteQueryConditionDto == null ? new AppSiteQuery() : appSiteQueryConditionDto;
        Page<AppSiteEntity> appSiteEntities = appSiteRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (condition.getSlbId() != null)
                Optional.of(condition.getSlbId()).ifPresent(o ->
                        predicates.add(criteriaBuilder.equal(root.get("slbId").as(Long.class), condition.getSlbId())));
            if (condition.getAppId() != null)
                Optional.of(condition.getAppId()).ifPresent(o ->
                        predicates.add(criteriaBuilder.equal(root.get("appId").as(String.class), condition.getAppId())));
            if (condition.getName() != null)
                Optional.of(condition.getName()).ifPresent(o ->
                        predicates.add(criteriaBuilder.like(root.get("name").as(String.class), "%" + condition.getName() + "%")));
            if (condition.getDomain() != null)
                Optional.of(condition.getDomain()).ifPresent(o ->
                        predicates.add(criteriaBuilder.like(root.get("domain").as(String.class), "%" + condition.getDomain() + "%")));
            //添加isActive 为 true 的条件
            predicates.add(criteriaBuilder.equal(root.get("isActive").as(Boolean.class), true));

            //TODO 增加条件
            Predicate[] arrayPredicates = new Predicate[predicates.size()];
            return criteriaBuilder.and(predicates.toArray(arrayPredicates));
        }, page);

        return ConvertUtils.convertPage(appSiteEntities, AppSiteDto.class);
    }
}
