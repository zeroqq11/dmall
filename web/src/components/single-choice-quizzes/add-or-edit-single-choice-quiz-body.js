import React, { Component } from 'react'
import { Form, Input, Radio} from 'antd'
import { connect } from 'react-redux'
import {formItemLayout, radioStyle, inputStyle, mdLayout} from '../../constant/constant-style'
import { withRouter } from 'react-router-dom'
import * as actions from '../../actions/single-choice-quiz'
import UrlPattern from 'url-pattern'
import SelectTags from '../common/select-tags'
import '../../less/single-choice-quiz/index.less'
import { TwsReactMarkdownEditor } from 'tws-antd'
import ButtonGroup from '../common/button-group'

const FormItem = Form.Item
const RadioGroup = Radio.Group

class AddOrEditSingleChoiceQuizBody extends Component {
  constructor () {
    super()
    this.state = {
      id: -1,
      answer: '',
      choices: ['', '', '', ''],
      description: '',
      tags: [],
      choiceError: '',
      answerError: '',
      descriptionError: ''
    }
  }

  componentDidMount () {
    const pattern = new UrlPattern('/singleChoiceQuizzes/:id/editor')

    const urlParams = pattern.match(this.props.location.pathname) || {id: -1}
    this.setState({id: parseInt(urlParams.id)})
    if (urlParams.id > 0) {
      this.props.getSingleChoiceQuiz(urlParams.id)
    }
  }

  componentWillReceiveProps (nextProps) {
    const singleChoiceQuiz = nextProps.singleChoiceQuiz
    if (singleChoiceQuiz && singleChoiceQuiz !== this.props.singleChoiceQuiz) {
      const {tags, description, answer, choices, id} = singleChoiceQuiz
      this.setState({id, choices, answer, description, tags})
    }
  }

  isError (choices, answer, description) {
    let choiceError = ''
    let answerError = ''
    let descriptionError = ''
    if (Array.from(new Set(choices)).length !== 4) {
      choiceError = '选项不能重复'
    } else if (choices.includes('')) {
      choiceError = '选项不能为空'
    }

    if (!answer && answer !== 0) {
      answerError = '答案不能为空'
    }

    if (description === '') {
      descriptionError = '描述不能为空'
    }

    if (choiceError !== '' || answerError !== '' || descriptionError !== '') {
      this.setState({choiceError: choiceError, answerError, descriptionError})
      return true
    }

    return false
  }

  cleanStateOrBack (isBack) {
    isBack ? this.props.history.push('/singleChoiceQuizzes') : this.setState({
      answer: '',
      choices: ['', '', '', ''],
      description: '',
      tags: [],
      choiceError: '',
      answerError: '',
      descriptionError: ''
    })
  }

  handleSubmit (isBack) {
    const {choices, answer, id, description, tags} = this.state
    if (!this.isError(choices, answer, description)) {
      const values = Object.assign({}, this.props.singleChoiceQuiz, {choices, answer, description, tags})
      const editValues = Object.assign({}, values, {id})

      id > 0 ? this.props.editSingleChoiceQuiz(editValues, () => this.cleanStateOrBack(isBack)) : this.props.addSingleChoiceQuiz(values, () => this.cleanStateOrBack(isBack))
    }
  }

  radioOnChange (e) {
    this.setState({
      answer: e.target.value,
      answerError: ''
    })
  }

  optionOnChange (index, e) {
    const {choices} = this.state
    choices[index] = e.target.value
    this.setState({
      choices,
      choiceError: ''
    })
  }

  render () {
    const {choices, answer, choiceError, answerError, description, descriptionError, tags, id} = this.state

    return (
      <div>
        <Form>
          <FormItem
            {...mdLayout}
            label={'题目描述'}
          >
            <TwsReactMarkdownEditor value={description} onChange={(description) => this.setState({description})} />
            <div className='error-tip'>{descriptionError}</div>
          </FormItem>

          <FormItem {...formItemLayout}
            label={'选项'} required>
            <RadioGroup onChange={this.radioOnChange.bind(this)} value={answer}>
              {choices.map((option, index) => {
                return (
                  <Radio style={radioStyle} className='margin-b-3' value={`${index}`} key={index}>
                    <Input style={inputStyle}
                      value={option}
                      onChange={this.optionOnChange.bind(this, index)}
                    />
                  </Radio>
                )
              })}
            </RadioGroup>
            <div className='error-tip'>{answerError || choiceError}</div>
          </FormItem>
          <SelectTags
            addTags={(tags) => this.setState({tags})}
            currentTags={tags}
          />
          <FormItem wrapperCol={{offset: 9}}>
            <ButtonGroup handleSubmit={this.handleSubmit.bind(this)} id={id} />
          </FormItem>
        </Form>
      </div>
    )
  }
}

const mapStateToProps = ({singleChoice}) => ({
  singleChoiceQuiz: singleChoice.quiz
})

const mapDispatchToProps = dispatch => ({
  addSingleChoiceQuiz: (quiz, callback) => dispatch(actions.addQuiz(quiz, callback)),
  getSingleChoiceQuiz: (quizId) => dispatch(actions.getQuiz(quizId)),
  editSingleChoiceQuiz: (quiz, callback) => dispatch(actions.editQuiz(quiz, callback))
})

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(Form.create()(AddOrEditSingleChoiceQuizBody)))
