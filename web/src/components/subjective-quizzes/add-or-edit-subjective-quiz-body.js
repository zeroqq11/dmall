import React from 'react'
import UrlPattern from 'url-pattern'
import { withRouter } from 'react-router-dom'
import { Form, Input } from 'antd'
import { connect } from 'react-redux'

import { formItemLayout, mdLayout } from '../../constant/constant-style'
import SelectTags from '../common/select-tags'
import * as actions from '../../actions/subjective-quizzes'
import { TwsReactMarkdownEditor } from 'tws-antd'
import ButtonGroup from '../common/button-group'

const FormItem = Form.Item

class AddOrEditSubjectiveQuizBody extends React.Component {
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
    const pattern = new UrlPattern('/subjectiveQuizzes/:id/editor')
    const urlParams = pattern.match(this.props.location.pathname) || {id: -1}
    this.setState({id: parseInt(urlParams.id)})
    let description = window.localStorage.getItem('description')
    if (description) {
      this.setState({description})
    }
    if (urlParams.id > 0) {
      this.props.getSubjectiveQuiz(parseInt(urlParams.id))
    }
  }

  componentWillReceiveProps (nextProps) {
    const {subjectiveQuiz} = nextProps

    if (this.state.id > 0 && subjectiveQuiz && this.props.subjectiveQuiz !== subjectiveQuiz) {
      this.setState({description: subjectiveQuiz.description, tags: subjectiveQuiz.tags})
      const tagsNames = subjectiveQuiz.tags.map(tag => tag.name)
      nextProps.form.setFieldsValue({
        tags: tagsNames
      })
    }
  }

  cleanStateOrBack (isBack) {
    console.log(isBack)
    isBack ? this.props.history.push('/subjectiveQuizzes') : this.setState({
      description: '',
      descriptionError: '',
      tags: []
    })
  }

  handleSubmit (isBack) {
    if (!this.state.description) {
      this.setState({descriptionError: '描述不能为空'})
    } else {
      this.props.form.validateFields((err, values) => {
        if (!err) {
          const {id, description, tags} = this.state
          let localStorageDescription = window.localStorage.getItem('description')
          if (localStorageDescription) {
            window.localStorage.removeItem('description')
          }
          const quiz = Object.assign({}, values, {id, description, tags})
          id > 0 ? this.props.editSubjectiveQuiz(quiz, () => this.cleanStateOrBack(isBack)) : this.props.addSubjectiveQuiz(quiz, () => this.cleanStateOrBack(isBack))
        }
      })
    }
  }

  changeDescription (description) {
    let storage = window.localStorage
    this.setState({description}, () => {
      storage.setItem('description', this.state.description)
    })
  }

  render () {
    const {getFieldDecorator} = this.props.form
    const {descriptionError, tags, id} = this.state
    return (<Form>
      <FormItem
        {...mdLayout}
        label='题目描述' required
      >
        <TwsReactMarkdownEditor value={this.state.description}
          onChange={this.changeDescription.bind(this)} />
        <div className='error-tip'>{descriptionError}</div>
      </FormItem>
      <FormItem
        {...formItemLayout}
        label='备注'
      >
        {
          getFieldDecorator('remark')(<Input />)
        }
      </FormItem>
      <SelectTags addTags={(tags) => this.setState({tags})} currentTags={tags} />
      <FormItem wrapperCol={{offset: 9}}>
        <ButtonGroup handleSubmit={this.handleSubmit.bind(this)} id={id} />
      </FormItem>
    </Form>)
  }
}
const mapStateToProps = (state) => ({
  subjectiveQuiz: state.subjective.quiz
})

const mapDispatchToProps = (dispatch) => ({
  addSubjectiveQuiz: (subjectiveQuiz, callback) => dispatch(actions.addQuiz(subjectiveQuiz, callback)),
  getSubjectiveQuiz: (quizId) => dispatch(actions.getQuiz(quizId)),
  editSubjectiveQuiz: (quiz, callback) => dispatch(actions.editQuiz(quiz, callback))
})

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(Form.create()(AddOrEditSubjectiveQuizBody)))
