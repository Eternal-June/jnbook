import request from '@/utils/request'

export function listbooks(query) {
  return request({
    url: '/books/list',
    method: 'get',
    params: query
  })
}

export function deletebooks(data) {
  return request({
    url: '/books/delete',
    method: 'post',
    data
  })
}

export function publishbooks(data) {
  return request({
    url: '/books/create',
    method: 'post',
    data
  })
}

export function detailbooks(id) {
  return request({
    url: '/books/detail',
    method: 'get',
    params: { id }
  })
}

export function editbooks(data) {
  return request({
    url: '/books/update',
    method: 'post',
    data
  })
}

export function listCatAndBrand() {
  return request({
    url: '/books/catAndBrand',
    method: 'get'
  })
}
