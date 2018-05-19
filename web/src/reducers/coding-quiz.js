import { STATUS } from '../constant/constant-data'

const init = {
  totalElements: 0,
  content: []
}

const initStatus = {
  code: 1, logs: ''
}

export default (state = {quizzes: init, quiz: {}, id: 0, status: initStatus}, action) => {

  switch (action.type) {
    case 'ADD_CODING_QUIZ':

      return Object.assign({}, state, {id: action.data, status: {...initStatus}})

    case 'EDIT_CODING_QUIZ':
      return Object.assign({}, state, {id: action.data, status: {...initStatus}})

    case 'GET_CODING_QUIZ':
      return Object.assign({}, state, {quiz: {...action.data}})

    case 'GET_CODING_QUIZZES':
      return Object.assign({}, state, {quizzes: {...action.data}})

    case 'GET_CODING_LOGS':
      let status = state.status

      status = Object.assign({}, status, {code: action.code, logs: action.logs})

      return Object.assign({}, state, {status: {...status}})

    default:
      return state
  }
}
