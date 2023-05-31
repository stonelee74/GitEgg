import request from '@/utils/request'
import defaultSettings from '@/config/defaultSettings'

export function simpleList (query) {
  return request({
    url: `${defaultSettings.service_system_url}user/simpleList`,
    method: 'get',
    params: query
  })
}

export function fetchList (query) {
  return request({
    url: `${defaultSettings.service_system_url}user/list`,
    method: 'get',
    params: query
  })
}

export function createUser (data) {
  return request({
    url: `${defaultSettings.service_system_url}user/create`,
    method: 'post',
    data
  })
}

export function updateUser (data) {
  return request({
    url: `${defaultSettings.service_system_url}user/update`,
    method: 'post',
    data
  })
}

export function updateUserInfo (data) {
  return request({
    url: `${defaultSettings.service_system_url}user/update/info`,
    method: 'post',
    data
  })
}

export function queryUserInfo (data) {
  return request({
    url: `${defaultSettings.service_system_url}auth/user/info`,
    method: 'get',
    data
  })
}

export function updatePwd (data) {
  return request({
    url: `${defaultSettings.service_system_url}user/password/change`,
    method: 'post',
    data
  })
}

export function resetUserPassword (id) {
  return request({
    url: `${defaultSettings.service_system_url}user/password/reset/${id}`,
    method: 'post'
  })
}

export function updateUserStatus (userId, status) {
  return request({
    url: `${defaultSettings.service_system_url}user/status/${userId}/${status}`,
    method: 'post'
  })
}

export function deleteRole (id) {
  return request({
    url: `${defaultSettings.service_system_url}user/deleteRole/${id}`,
    method: 'post'
  })
}

export function deleteUser (id) {
  return request({
    url: `${defaultSettings.service_system_url}user/deleteUser/${id}`,
    method: 'post'
  })
}

export function batchDeleteUser (data) {
  return request({
    url: `${defaultSettings.service_system_url}user/batch/delete`,
    method: 'post',
    data
  })
}

export function fetchRoleList (data) {
  return request({
    url: `${defaultSettings.service_system_url}role/all`,
    method: 'get',
    data
  })
}

export function updateUserDataPermission (data) {
  return request({
    url: `${defaultSettings.service_system_url}user/update/organization/data/permission`,
    method: 'post',
    data
  })
}

export function checkUserExist (data) {
  return request({
    url: `${defaultSettings.service_system_url}user/check`,
    method: 'post',
    data
  })
}

export function fetchOrganizationDataList (query) {
  return request({
    url: `${defaultSettings.service_system_url}user/organization/data/permission/list`,
    method: 'get',
    params: query
  })
}

export function batchDeleteOrganizationData (data) {
  return request({
    url: `${defaultSettings.service_system_url}user/organization/data/permission/batch/delete`,
    method: 'post',
    data
  })
}
