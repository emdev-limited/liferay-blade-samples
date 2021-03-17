import React, {useEffect, useState} from 'react';
import UserCard from './UserCard';
// import { useQuery } from '@apollo/react-hooks';
// import { gql } from 'apollo-boost';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClayAlert from '@clayui/alert';

export default function UserCardList() {

  let [currentPage, setCurrentPage] = useState(1);
  let [portionNumber, setPortionNumber] = useState(1);
  let [items, setItems] = useState(null)
  let [usersCount, setUsersCount] = useState(null)

  // const ALL_USERS = gql`
  // query{
  //   userAccounts(page: ${currentPage}, pageSize: ${5}) {
  //     items {
  //       name,
  //       alternateName,
  //       id,
  //       image
  //     }
  //     page
  //     pageSize
  //     totalCount
  //   }
  // }
  // `;
  useEffect(() => {
    console.log('useEffect')
    Liferay.Service(
        '/user/get-company-users',
        {
          companyId: Liferay.ThemeDisplay.getCompanyId(),
          start: currentPage,
          end: 5
        },
        function(obj) {
          console.log(obj);

          setItems(obj)

        }
    );
  }, [currentPage])

  useEffect(() => {
    Liferay.Service(
        '/user/get-company-users-count',
        {
          companyId: Liferay.ThemeDisplay.getCompanyId()
        },
        function(obj) {
          console.log(obj);
          setUsersCount(obj)
        }
    );
  }, [])

  // const {loading, error, data} = useQuery(ALL_USERS);

  // if (loading) return <ClayLoadingIndicator />;
  // if (error) {
  //   return (
  //     <ClayAlert displayType="danger" title="Error:">
  //       An error occured while loading data.
  //       <div className="mt-2">
  //         <code>{error.message}</code>
  //       </div>
  //     </ClayAlert>
  //   );
  // }

  if (!items) {
    return <ClayLoadingIndicator />

  }
  // let {items} = data.userAccounts;
  // let {totalCount} = data.userAccounts;
  // let {pageSize} = data.userAccounts;

  // let {page} = data.userAccounts;

  if (items.length === 0) {
    return (
      <ClayAlert displayType="info" className="text-center">
        {Liferay.Language.get("this-organization-does-not-have-any-users")}
      </ClayAlert>
    );
  }

  const cards = items.map(
      ({firstName, contactId, portraitId}) => (
          <UserCard
            imageId={portraitId}
            key={contactId}
            name={firstName}
            // alternateName={alternateName}
          />
      )
  );

  if (!usersCount) {
    return <ClayLoadingIndicator />
  }

    let pageSize = 5
    let pagesCount = Math.ceil(usersCount / pageSize);
    let pages = [];
    console.log(pagesCount)

    for (let i = 1; i <= pagesCount; i++) {
        pages.push(i);
    }

    let portionSize = 3;
    // let page = 3;
    let portionCount = Math.ceil(pagesCount / portionSize);
    let leftPortionPageNumber = (portionNumber - 1) * portionSize + 1;
    let rightPortionPageNumber  = portionNumber * portionSize;

  return <React.Fragment> <div className="row">{cards}</div>

    <hr/>
    <div>
        { currentPage > 1 && <span className={'icon-angle'} onClick={() => {
          if (portionNumber === 1) {
            setCurrentPage(1);
          } else {
            setPortionNumber(portionNumber - 1);
            setCurrentPage(leftPortionPageNumber - portionSize);
          }
        }}>
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

        {portionCount > portionNumber && <span className={'icon-angle'} onClick={() => {
            setPortionNumber(portionNumber + 1);
            setCurrentPage(rightPortionPageNumber + 1);
        }}>{<svg className="lexicon-icon lexicon-icon-angle-right" focusable="false" role="presentation" viewBox="0 0 420 420">
          <use href={Liferay.ThemeDisplay.getPathThemeImages() + "/lexicon/icons.svg#caret-right"}/>
        </svg>}</span> }
    </div>
  </React.Fragment>
}
