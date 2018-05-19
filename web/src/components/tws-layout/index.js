import React, {Component} from 'react'
import {Layout, Row, Col} from 'antd'

import TwsUserInfo from './tws-user-info'
import LeftMenu from './left-menu'
import TwsBreadCrumb from './tws-bread-crumb'
import logo from '../../images/logo-white.png'
import {withRouter} from 'react-router-dom'

const { Header, Content, Footer, Sider } = Layout

class TwsLayout extends Component {
  render () {
    return (
      <Layout>
        <Header className='App-header' style={{lineHeight: '36px'}}>
          <Row>
            <Col span={8}>
              <img src={logo} className='App-logo' alt='logo' />
            </Col>
            <Col span={16}>
              <TwsUserInfo />
            </Col>
          </Row>
        </Header>
        <Content style={{ padding: '0 50px' }}>
          <TwsBreadCrumb />
          <Layout style={{ padding: '24px 0', background: '#fff' }}>
            <Sider width={200} style={{ background: '#fff' }}>
              <LeftMenu />
            </Sider>
            <Content style={{ padding: '0 24px', minHeight: 280 }}>
              {this.props.children}
            </Content>
          </Layout>
        </Content>
        <Footer style={{ textAlign: 'center' }}>
            ThoughtWorks School ©2018
          </Footer>
      </Layout>
    )
  }
}

export default withRouter(TwsLayout)
