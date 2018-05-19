import { combineReducers } from 'redux'
import userInfo from './user'
import quizzes from './quizzes'
import multipleChoice from './multiple-choice-quiz'
import subjective from './subjective-quiz'
import singleChoice from './single-choice-quiz'
import blank from './blank-quiz'
// import logicQuizzes from './logic-quizzes'
import logicQuizData from './logic-quiz'
import coding from './coding-quiz'
import tagData from './tag'
import stackData from './stack'
import searchTags from './search-tags'

export default combineReducers({
  userInfo,
  multipleChoice,
  quizzes,
  subjective,
  singleChoice,
  blank,
  tagData,
  logicQuizData,
  coding,
  stackData,
  searchTags
})
