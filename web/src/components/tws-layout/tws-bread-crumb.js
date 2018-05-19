import React, { Component } from 'react'
import { Breadcrumb } from 'antd'
import sidebar from '../../constant/sidebar'
import { withRouter } from 'react-router-dom'
import BREADCRUMB from '../../constant/breadcrumb'

const defaultBreadCrumbs = ['思特沃克学院', '题库']

class TwsBreadCrumb extends Component {
  constructor () {
    super()
    this.state = {
      breadCrumbs: defaultBreadCrumbs
    }
  }

  componentDidMount () {
    const {history, location} = this.props
    history.listen(this.update.bind(this))
    this.update(location)
  }

  update (location) {
    const bar = sidebar.find((item) => {
      return item.linkTo === location.pathname
    })
    if (!bar) {
      return
    }
    const breadCrumbs = [].concat(defaultBreadCrumbs, bar.name)
    this.setState({
      breadCrumbs
    })
  }

  changePage (item) {
    if (item === '思特沃克学院') {
      window.location.href = BREADCRUMB[item]
    } else {
      this.props.history.push(BREADCRUMB[item])
    }
  }

  render () {
    return (
      <Breadcrumb style={{margin: '12px 0'}}>
        {
          this.state.breadCrumbs.map((item, index) => {
            return index + 1 === this.state.breadCrumbs.length
                ? <Breadcrumb.Item key={index}>{item}</Breadcrumb.Item>
                : <Breadcrumb.Item key={index}><a onClick={this.changePage.bind(this, item)}>{item}</a></Breadcrumb.Item>
          })
        }
      </Breadcrumb>
    )
  }
}

export default withRouter(TwsBreadCrumb)
