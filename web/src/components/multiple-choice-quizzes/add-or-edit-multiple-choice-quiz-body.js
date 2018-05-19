import React from 'react'
import {withRouter} from 'react-router-dom'
import UrlPattern from 'url-pattern'
import {Form, Input, Checkbox, Row, Col} from 'antd'
import {connect} from 'react-redux'
import {formItemLayout, mdLayout} from '../../constant/constant-style'
import * as actions from '../../actions/multiple-choice-quiz'
import SelectTags from '../common/select-tags'
import {MAP_CHECKOUT_COUNT} from '../../constant/constant-data'
import {TwsReactMarkdownEditor} from 'tws-antd'
import ButtonGroup from '../common/button-group'
const FormItem = Form.Item

class AddOrEditMultipleChoiceQuizBody extends React.Component {
  constructor (props) {
    super(props)
    this.state = {
      id: -1,
      choices: ['', '', '', ''],
      answers: [],
      errors: [false, false, false, false],
      description: '',
      tags: [],
      descriptionError: ''
    }
  }

  componentWillMount () {
    const pattern = new UrlPattern('/multipleChoiceQuizzes/:id/editor')
    const urlParams = pattern.match(this.props.location.pathname) || {id: -1}
    this.setState({id: parseInt(urlParams.id)})
    if (urlParams.id > 0) {
      this.props.getMultipleChoiceQuiz(parseInt(urlParams.id))
    }
  }

  componentWillReceiveProps (nextProps) {
    const {multipleChoiceQuiz} = nextProps

    if (multipleChoiceQuiz && this.props.multipleChoiceQuiz !== multipleChoiceQuiz) {
      const {choices, answer, tags, description} = multipleChoiceQuiz

      this.setState({choices, answers: answer, description, tags})
    }
  }

  errorDisplay () {
    const {choices, answers, errors, description} = this.state

    choices.forEach((option, index) => {
      if (option === '') {
        document.getElementById(`error${index}`).innerHTML = '<span>请输入选项</span>'
        errors[index] = true
        this.setState({errors})
      }
    })

    if (answers.length === 0) {
      document.getElementById(`error`).innerHTML = '<span>请选择答案</span>'
    }

    if (description.length === 0) {
      this.setState({
        descriptionError: '请输入描述'
      })
    }
  }

  cleanStateOrBack (isBack) {
    isBack ? this.props.history.push('/multipleChoiceQuizzes') : this.setState({
      choices: ['', '', '', ''],
      answers: [],
      errors: [false, false, false, false],
      description: '',
      tags: [],
      descriptionError: ''})
  }

  handleSubmit (isBack) {
    const {choices, answers, errors, id, description, tags} = this.state

    this.errorDisplay()

    if (answers.length !== 0 && !errors.find(err => err) && description.length !== 0) {
      const values = Object.assign({}, {choices, answer: answers, description, tags})
      id > 0 ? this.props.editMultipleChoiceQuiz(values, id, () => this.cleanStateOrBack(isBack)) : this.props.addMultipleChoiceQuiz(values, () => this.cleanStateOrBack(isBack))
    }
  }

  checkboxOnChange (count, e) {
    const {answers} = this.state
    document.getElementById(`error`).innerHTML = ''

    if (e.target.checked) {
      answers.push(count)
    } else {
      const index = answers.indexOf(count)
      answers.splice(index, 1)
    }

    this.setState({
      answers
    })
  }

  optionOnChange (index, e) {
    const {choices, errors} = this.state
    const {value} = e.target
    document.getElementById(`error${index}`).innerHTML = ''

    if (choices.some(option => option === value) || value === '') {
      document.getElementById(`error${index}`).innerHTML = value === '' ? '<span>请输入选项</span>' : '<span>选项重复，请重新填写</span>'
      errors[index] = true
    } else {
      errors[index] = false
    }

    choices[index] = e.target.value

    this.setState({
      choices,
      errors
    })
  }

  render () {
    const {answers, choices, errors, description, tags, descriptionError, id} = this.state
    return (<Form>
      <FormItem
        {...mdLayout}
        label={'题目描述'}>
        <TwsReactMarkdownEditor value={description} onChange={(description) => this.setState({description, descriptionError: ''})} />
        <div style={{color: '#f5222d', lineHeight: '20px'}}>{descriptionError}</div>
      </FormItem>
      <FormItem
        {...formItemLayout}
        label={'选项'}
        required
      >
        {MAP_CHECKOUT_COUNT.map(count => {
          return (<Row key={count}>
            <Col span={2}>
              <Checkbox
                onChange={this.checkboxOnChange.bind(this, count.toString())}
                checked={answers.some(answer => parseInt(answer) === count)}
              />
            </Col>
            <Col span={12}>
              <Input
                className={errors[count] ? 'input-error' : ''}
                value={choices[count]}
                onChange={this.optionOnChange.bind(this, count)}
              />
              <div style={{color: '#f5222d', lineHeight: '20px'}} id={`error${count}`} />
            </Col>
          </Row>)
        })}
        <div style={{color: '#f5222d', lineHeight: '20px'}} id={`error`} />
      </FormItem>
      <SelectTags
        addTags={(tags) => this.setState({tags})}
        currentTags={tags}
      />
      <FormItem wrapperCol={{offset: 9}}>
        <ButtonGroup handleSubmit={this.handleSubmit.bind(this)} id={id} />
      </FormItem>
    </Form>)
  }
}

const mapStateToProps = (state) => ({
  multipleChoiceQuiz: state.multipleChoice.quiz
})

const mapDispatchToProps = dispatch => ({
  addMultipleChoiceQuiz: (quiz, callback) => dispatch(actions.addQuiz(quiz, callback)),
  getMultipleChoiceQuiz: (quizId) => dispatch(actions.getQuiz(quizId)),
  editMultipleChoiceQuiz: (quiz, id, callback) => dispatch(actions.editQuiz(quiz, id, callback))
})

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(Form.create()(AddOrEditMultipleChoiceQuizBody)))
