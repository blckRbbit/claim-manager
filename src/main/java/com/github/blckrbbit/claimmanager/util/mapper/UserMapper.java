package com.github.blckrbbit.claimmanager.util.mapper;

import com.github.blckrbbit.claimmanager.dto.UserDTO;
import com.github.blckrbbit.claimmanager.repository.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserDTO toModel(User user);
}
