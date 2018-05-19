import React, { Component } from 'react'
import { connect } from 'react-redux'
import { Icon, Menu, Dropdown, Button } from 'antd'

const menu = (
  <Menu>
    <Menu.Item>
      <a rel='noopener noreferrer' href='https://school.thoughtworks.cn/user-center/home/index.html'>
        <Icon type='user' className='margin-r-1' />个人中心
      </a>
    </Menu.Item>
    <Menu.Item>
      <a rel='noopener noreferrer' href='https://school.thoughtworks.cn/cas/logout'>
        <Icon type='close' className='margin-r-1' />退出
      </a>
    </Menu.Item>
  </Menu>
)

class TwsUserInfo extends Component {
  render () {
    const {user} = this.props
    return (
      <Dropdown overlay={menu} placement='bottomRight'>
        <Button style={{'float': 'right'}} className='margin-t-3'>{user.userName || '张三'} <Icon type='down' /></Button>
      </Dropdown>
    )
  }
}

const mapStateToProps = ({userInfo}) => ({user: userInfo.user})

export default connect(mapStateToProps)(TwsUserInfo)
