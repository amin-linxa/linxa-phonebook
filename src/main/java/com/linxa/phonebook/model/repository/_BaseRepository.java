package com.linxa.phonebook.model.repository;

import com.linxa.phonebook.model.entity._BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
interface _BaseRepository<T extends _BaseEntity> extends JpaRepository<T, Long> {
}
