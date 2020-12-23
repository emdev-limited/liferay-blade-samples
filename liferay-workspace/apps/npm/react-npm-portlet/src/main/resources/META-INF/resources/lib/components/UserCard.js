import React from 'react';
import {ClayCardWithUser} from '@clayui/card';
import { Liferay } from '../App';

export default function UserCard(props) {

  let [userPhoto, setUserPhoto] = React.useState(false);
 
  return (
    <div className="col-md-4">
      <ClayCardWithUser
        description={props.alternateName}
        href="#"
        name={props.name}
        spritemap={Liferay().ThemeDisplay.getPathThemeImages()+'/clay/icons.svg'}
        userImageSrc = {
          userPhoto ? '/userpic.png' : null
        }
      />
  </div>
  );
}
