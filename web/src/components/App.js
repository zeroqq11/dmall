import React, { Component } from 'react'
import TwsLayout from './tws-layout'
import '../less/index.less'

import {
  HashRouter as Router,
  Route
} from 'react-router-dom'
import QuizzesListBody from './quizzes/quizzes-list-body'
import SingleChoiceQuizzesListBody from './single-choice-quizzes/single-choice-quizzes-list-body'
import AddOrModifyMultipleChoiceQuizBody from './multiple-choice-quizzes/add-or-edit-multiple-choice-quiz-body'
// import MultipleChoiceQuizBody from './multiple-choice-quizzes/multiple-choice-quiz-body'
import MultipleChoiceQuizBody from './multiple-choice-quizzes/multiple-choice-quizzes-list'
import AddOrEditSingleChoiceQuizBody from './single-choice-quizzes/add-or-edit-single-choice-quiz-body'
import AddOrEditSubjectiveQuiz from './subjective-quizzes/add-or-edit-subjective-quiz-body'
import AddOrEditBlankQuiz from './blank-quizzes/add-or-edit-blank-quiz-body'
import CodingQuizzes from './coding-quizzes/coding-quizzes-list'
import BlankQuizzesList from './blank-quizzes/balnk-quizzes-list'
import AddOrEditCodingQuiz from './coding-quizzes/add-or-edit-coding-quiz'
import SubjectiveQuizList from './subjective-quizzes/subjective-quizzes-list'
import AddOrEditTags from './tags/add-or-edit-tags'
import TagsList from './tags/tags-list'
import LogicQuizzes from './logic-quizzes/logic-quizzes-list-body'
import AddOrEditLogicQuizzes from './logic-quizzes/add-or-edit-logic-quiz-body'
import StacksList from './stacks/stacks-list'
import AddStack from './stacks/add-stack'

export default class App extends Component {
  render () {
    return <Router>
      <TwsLayout>
        <Route exact path='/' component={QuizzesListBody} />
        <Route exact path='/quizzes' component={QuizzesListBody} />
        <Route exact path='/singleChoiceQuizzes' component={SingleChoiceQuizzesListBody} />
        <Route exact path='/singleChoiceQuizzes/new' component={AddOrEditSingleChoiceQuizBody} />
        <Route exact path='/singleChoiceQuizzes/:id/editor' component={AddOrEditSingleChoiceQuizBody} />
        <Route exact path='/multipleChoiceQuizzes/:id/editor' component={AddOrModifyMultipleChoiceQuizBody} />
        <Route exact path='/multipleChoiceQuizzes/new' component={AddOrModifyMultipleChoiceQuizBody} />
        <Route exact path='/multipleChoiceQuizzes' component={MultipleChoiceQuizBody} />
        <Route exact path='/subjectiveQuizzes/new' component={AddOrEditSubjectiveQuiz} />
        <Route exact path='/subjectiveQuizzes/:id/editor' component={AddOrEditSubjectiveQuiz} />
        <Route exact path='/blankQuizzes/new' component={AddOrEditBlankQuiz} />
        <Route exact path='/blankQuizzes/:id/editor' component={AddOrEditBlankQuiz} />
        <Route exact path='/codingQuizzes/' component={CodingQuizzes} />
        <Route exact path='/codingQuizzes/new' component={AddOrEditCodingQuiz} />
        <Route exact path='/codingQuizzes/:id/editor' component={AddOrEditCodingQuiz} />
        <Route exact path='/blankQuizzes' component={BlankQuizzesList} />
        <Route exact path='/subjectiveQuizzes' component={SubjectiveQuizList} />
        <Route exact path='/tags/new' component={AddOrEditTags} />
        <Route exact path='/tags/:id/editor' component={AddOrEditTags} />
        <Route exact path='/tags' component={TagsList} />
        <Route exact path='/logicQuizzes' component={LogicQuizzes} />
        <Route exact path='/logicQuizzes/new' component={AddOrEditLogicQuizzes} />
        <Route exact path='/logicQuizzes/:id/editor' component={AddOrEditLogicQuizzes} />
        <Route exact path='/stacks/new' component={AddStack} />
        <Route exact path='/stacks' component={StacksList} />
      </TwsLayout>
    </Router>
  }
}
