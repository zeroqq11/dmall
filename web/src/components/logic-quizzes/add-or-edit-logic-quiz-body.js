import React, {Component} from 'react'
import {Button, Col, Form, InputNumber, Row, Icon} from 'antd'
import {connect} from 'react-redux'
import {logicFormItemLayout} from '../../constant/constant-style'
import {withRouter} from 'react-router-dom'
import * as actions from '../../actions/logic-quiz'
import '../../less/single-choice-quiz/index.less'

const FormItem = Form.Item

class AddOrEditLogicQuizBody extends Component {
  cleanStateOrBack (isBack) {
    isBack ? this.props.history.push('/logicQuizzes') : this.props.form.resetFields()
  }

  handleSubmit (isBack) {
    this.props.form.validateFields((err, values) => {
      if (!err) { this.props.addLogicQuiz(values, () => this.cleanStateOrBack(isBack)) }
    })
  }

  render () {
    const {getFieldDecorator} = this.props.form

    return (
      <div>
        <Form>
          <Row>
            <Col span={4} offset={6}>
              <FormItem {...logicFormItemLayout} label={(<b> 简单 </b>)}>
                {getFieldDecorator('easyCount', {
                  rules: [{
                    required: true, message: '请输入简单题个数'
                  }]
                })(
                  <InputNumber mode='easyCount'
                    min={0} />
                )}
              </FormItem>
            </Col>
            <Col span={4}>
              <FormItem {...logicFormItemLayout} label={(<b> 一般 </b>)}>
                {getFieldDecorator('normalCount', {
                  rules: [{
                    required: true, message: '请输入一般题个数'
                  }]
                })(
                  <InputNumber mode='normalCount'
                    min={0} />
                  )}
              </FormItem>
            </Col>
            <Col span={4}>
              <FormItem {...logicFormItemLayout} label={(<b> 困难 </b>)}>
                {getFieldDecorator('hardCount', {
                  rules: [{
                    required: true, message: '请输入困难题个数'
                  }]
                })(
                  <InputNumber mode='hardCount'
                    min={0} />
                  )}
              </FormItem>
            </Col>
          </Row>
          <FormItem wrapperCol={{offset: 9}}>
            <Button type='primary' ghost style={{marginRight: 40}} onClick={this.handleSubmit.bind(this, true)}><Icon type='left' />添加并返回</Button>
            <Button type='primary' onClick={this.handleSubmit.bind(this, false)}>添加并继续<Icon type='right' /></Button>
          </FormItem>
        </Form>
      </div>
    )
  }
}

const mapStateToProps = ({logicQuiz}) => ({
  logicQuiz
})

const mapDispatchToProps = dispatch => ({
  addLogicQuiz: (quiz, callback) => dispatch(actions.addLogicQuiz(quiz, callback))
  // getLogicQuiz: (quizId) => dispatch(actions.getLogicQuizzes(quizId))
  // editLogicQuiz: (quiz) => dispatch(actions.editQuiz(quiz))
})

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(Form.create()(AddOrEditLogicQuizBody)))
