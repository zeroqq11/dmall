import React from 'react'
import { withRouter } from 'react-router-dom'
import UrlPattern from 'url-pattern'
import { Form, Input } from 'antd'
import { connect } from 'react-redux'

import {formItemLayout, mdLayout} from '../../constant/constant-style'
import SelectTags from '../common/select-tags'
import * as actions from '../../actions/blank-quiz'
import { TwsReactMarkdownEditor } from 'tws-antd'
import ButtonGroup from '../common/button-group'

const FormItem = Form.Item

class AddOrEditBlankQuizBody extends React.Component {
  constructor (props) {
    super(props)
    this.state = {
      id: -1,
      description: '',
      descriptionError: '',
      tags: []
    }
  }

  componentWillMount () {
    const pattern = new UrlPattern('/blankQuizzes/:id/editor')
    const urlParams = pattern.match(this.props.location.pathname) || {id: -1}
    this.setState({id: parseInt(urlParams.id)})

    if (urlParams.id > 0) {
      this.props.getBlankQuiz(parseInt(urlParams.id))
    }
  }

  componentWillReceiveProps (nextProps) {
    const {blankQuiz} = nextProps

    if (this.state.id > 0 && blankQuiz && this.props.blankQuiz !== blankQuiz) {
      this.setState({description: blankQuiz.description, tags: blankQuiz.tags})
      nextProps.form.setFieldsValue({
        answer: blankQuiz.answer
      })
    }
  }

  cleanStateOrBack (isBack) {
    isBack ? this.props.history.push('/blankQuizzes') : this.setState({
      description: '',
      descriptionError: '',
      tags: []
    }, () => {
      this.props.form.resetFields()
    })
  }

  handleSubmit (isBack) {
    this.setState({descriptionError: ''})

    if (this.state.description === '') {
      this.setState({descriptionError: '请输入描述'})
    } else {
      this.props.form.validateFields((err, values) => {
        if (!err) {
          const {id, description, tags} = this.state
          const quiz = Object.assign({}, values, {id, description, tags})

          id > 0 ? this.props.editBlankQuiz(quiz, () => this.cleanStateOrBack(isBack)) : this.props.addBlankQuiz(quiz, () => this.cleanStateOrBack(isBack))
        }
      })
    }
  }

  render () {
    const { getFieldDecorator } = this.props.form
    const {id, description, tags, descriptionError} = this.state
    return (<Form>
      <FormItem
        {...mdLayout}
        label='题目描述' required
      >
        <TwsReactMarkdownEditor value={description}
          onChange={(description) => this.setState({description})} />
        <div className='error-tip'>{descriptionError}</div>
      </FormItem>
      <FormItem
        {...formItemLayout}
        label='答案'
      >
        {getFieldDecorator('answer', {
          rules: [{
            required: true, message: '请输入答案'
          }]
        })(
          <Input />
        )}
      </FormItem>
      <SelectTags addTags={(tags) => this.setState({tags})} currentTags={tags} />
      <FormItem wrapperCol={{offset: 9}}>
        <ButtonGroup handleSubmit={this.handleSubmit.bind(this)} id={id} />
      </FormItem>
    </Form>)
  }
}

const mapStateToProps = ({blank}) => ({
  blankQuiz: blank.quiz
})

const mapDispatchToProps = (dispatch) => ({
  addBlankQuiz: (quiz, callback) => dispatch(actions.addQuiz(quiz, callback)),
  getBlankQuiz: (quizId) => dispatch(actions.getQuiz(quizId)),
  editBlankQuiz: (quiz, callback) => dispatch(actions.editQuiz(quiz, callback))
})

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(Form.create()(AddOrEditBlankQuizBody)))
