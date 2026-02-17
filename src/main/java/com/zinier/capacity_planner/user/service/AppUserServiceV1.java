package com.zinier.capacity_planner.user.service;

import com.zinier.capacity_planner.user.dto.AppUserDtoV1;
import com.zinier.capacity_planner.user.model.AppUserResponseModel;

import java.util.List;

public interface AppUserServiceV1 {

    List<AppUserResponseModel> listAll();

    AppUserResponseModel getById(Long id);

    AppUserResponseModel create(AppUserDtoV1 request);

    AppUserResponseModel update(Long id, AppUserDtoV1 request);

    void delete(Long id);
}
