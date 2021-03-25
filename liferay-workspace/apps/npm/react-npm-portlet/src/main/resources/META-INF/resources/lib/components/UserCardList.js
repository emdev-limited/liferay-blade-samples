import React, {useEffect, useState} from 'react';
import UserCard from './UserCard';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClayAlert from '@clayui/alert';

export default function UserCardList() {

  const [items, setItems] = useState([]);
  const [usersCount, setUsersCount] = useState(null);
  const [isFetching, setIsFetching] =  useState(false);
  const pageSize = 6;

  const href = window.location.href;
  let res;
  res = Number(href.split('=').pop());
  if (!res) {
    res = 1;
  }

  const [currentPage, setCurrentPage] = useState(res);
  const [portionNumber, setPortionNumber] = useState(1);
  const portionSize = 5;
  const rightPortionPageNumber  = portionNumber * portionSize;

  useEffect(() => {
    setIsFetching(true);

    if (currentPage !== 1) {
      window.history.pushState('', '', `?cur=${currentPage}`);
    } else {
      window.history.pushState('', '', location.pathname);
    }
    if (currentPage > rightPortionPageNumber) {
      setPortionNumber(portionNumber + 1);
    }
    setCurrentPage(currentPage);

    Liferay.Service(
        '/user/get-company-users',
        {
          companyId: Liferay.ThemeDisplay.getCompanyId(),
          start: (currentPage - 1) * pageSize + 1,
          end: currentPage * pageSize + 1
        },
        function(obj) {
          setIsFetching(false);
          setItems(obj);
        }
    );

    Liferay.Service(
        '/user/get-company-users-count',
        {
          companyId: Liferay.ThemeDisplay.getCompanyId()
        },
        function(obj) {
          setIsFetching(false);
          setUsersCount(obj);
        }
    );
  }, [currentPage]);


  if (isFetching) return <ClayLoadingIndicator />;

  if (items.length === 0) {
    return (
      <ClayAlert displayType="info" className="text-center">
        {Liferay.Language.get("this-organization-does-not-have-any-users")}
      </ClayAlert>
    );
  }

  const cards = items.map(
      ({firstName, lastName, middleName, contactId, portraitId}) => (
          <UserCard
              imageId={portraitId}
              key={contactId}
              firstName={firstName}
              lastName={lastName}
              middleName={middleName}
          />
      )
  );

  let pagesCount = Math.ceil(usersCount / pageSize);
  let pages = [];

  for (let i = 1; i <= pagesCount; i++) {
      pages.push(i);
  }

  let portionCount = Math.ceil(pagesCount / portionSize);
  let leftPortionPageNumber = (portionNumber - 1) * portionSize + 1;

  const leftHandleClickPage = () => {
    if (currentPage === 1) {
      return false
    }
    if (currentPage === leftPortionPageNumber) {
      setPortionNumber(portionNumber - 1);
    }
    setCurrentPage(currentPage - 1);
  };
  const rightHandleClickPage = () => {
    if (currentPage === rightPortionPageNumber) {
      setPortionNumber(portionNumber + 1);
    }
    setCurrentPage(currentPage + 1);
  };

  return <React.Fragment> <div className="row">{cards}</div>

    <hr/>
    <div>
      {<span className={ currentPage > 1 ? 'icon-angle' : 'icon-angle disabled'} onClick={leftHandleClickPage}>
          {<svg className="lexicon-icon lexicon-icon-angle-left" focusable="false" role="presentation" viewBox="0 0 420 420">
            <use href={Liferay.ThemeDisplay.getPathThemeImages() + "/lexicon/icons.svg#caret-left"}/>
          </svg>}
        </span>}
        <b>{pages.filter(p => p >= leftPortionPageNumber && p <= rightPortionPageNumber)
            .map((p, idx) => {
                return <span className={p === currentPage
                    ? 'usersPaginationBtns currenPage'
                    :'usersPaginationBtns'}
                             key={idx}
                             onClick={() => setCurrentPage(p)}>{p}</span>
            })}</b>

      {portionCount > portionNumber && <span className={'icon-angle'} onClick={rightHandleClickPage}>{<svg className="lexicon-icon lexicon-icon-angle-right" focusable="false" role="presentation" viewBox="0 0 420 420">
        <use href={Liferay.ThemeDisplay.getPathThemeImages() + "/lexicon/icons.svg#caret-right"}/>
      </svg>}</span> }
    </div>
  </React.Fragment>
}
