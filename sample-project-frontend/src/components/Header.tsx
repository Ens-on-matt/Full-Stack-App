import React, {FC, useState} from 'react'
import {Button, Col, Container, Form, Nav, Navbar, Row} from "react-bootstrap";
import DatabaseTypes from "../assets/DatabaseTypes.tsx";

interface props {
    toggleModal: (show: boolean) => void;
    submitSearchTerm: (searchTerm: string) => void;
    database: string;
}

// Creates the header at the top of the page to let users navigate between staff, students, courses, degrees
// alongside elements to let users use additional functionality like search
const Header:FC<props> = ( {toggleModal, submitSearchTerm, database}) => {
    const [searchTerm, setSearchTerm] = useState('');

    const searchOnChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setSearchTerm(event.target.value);
    }

    const searchOnSubmit = (event: React.ChangeEvent<HTMLFormElement>) => {
        event.preventDefault();
        submitSearchTerm(searchTerm);
        setSearchTerm('');
    }

    const sentenceCaseDatabase = database.charAt(0).toUpperCase() + database.substring(1);

    return (
    <header className='header'>
        <Navbar className='bg-body-tertiary '>
            <Container>
                <Navbar.Brand className='header-btn' href="/">HOME</Navbar.Brand>
                <Nav.Link className='header-btn' href="/staff">STAFF</Nav.Link>
                <Nav.Link className='header-btn' href="/student">STUDENT</Nav.Link>
                <Nav.Link className='header-btn' href="/course">COURSE</Nav.Link>
                <Nav.Link className='header-btn' href="/degree">DEGREE</Nav.Link>
            </Container>
        </Navbar>
        { database != DatabaseTypes.HOME ?
        <div className='container'>
            <h2>{sentenceCaseDatabase} Database</h2>
            <h3/><h3/><h3/>
            <Form className='d-flex' onSubmit={searchOnSubmit}>
                <Row>
                    <Col xs="auto">
                        <Form.Control
                            type="text"
                            placeholder="Name"
                            className=" mr-sm-2"
                            onChange={searchOnChange}
                            value={searchTerm}
                        />
                    </Col>
                    <Col xs="auto">
                        <Button type="submit">Search</Button>
                    </Col>
                </Row>
            </Form>

            <button onClick={() => toggleModal(true)} className='btn'>
                <i className='bi bi-plus-square'></i> Add New {sentenceCaseDatabase}
            </button>
        </div> : <div className='container'></div>}
    </header>
    )
}

export default Header